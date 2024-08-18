package me.earth.earthhack.impl.modules.player.arrows;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpectralArrowItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Arrows extends RegisteringModule<Boolean, SimpleRemovingSetting>
{
    protected static Potion SPECTRAL;
    /** Potions that don't give lasting Effects */
    protected static final Set<Potion> BAD_TYPES = Sets.newHashSet(
            Potions.EMPTY,
            Potions.WATER,
            Potions.MUNDANE,
            Potions.THICK,
            Potions.AWKWARD,
            Potions.HEALING,
            Potions.STRONG_HEALING,
            Potions.STRONG_HARMING,
            Potions.HARMING
    );

    protected final Setting<Boolean> shoot =
        register(new BooleanSetting("Shoot", false));
    protected final Setting<Boolean> cycle =
        register(new BooleanSetting("Cycle-Shoot", true));
    protected final Setting<Boolean> autoRelease =
        register(new BooleanSetting("Auto-Release", false));
    protected final Setting<Integer> releaseTicks =
        register(new NumberSetting<>("Release-Ticks", 3, 0, 20));
    protected final Setting<Integer> maxTicks =
        register(new NumberSetting<>("Max-Ticks", 10, 0, 20));
    protected final Setting<Boolean> tpsSync =
        register(new BooleanSetting("Tps-Sync", true));
    protected final Setting<Integer> cancelTime =
        register(new NumberSetting<>("Cancel-Time", 0, 0, 500));
    protected final Setting<Integer> delay =
        register(new NumberSetting<>("Cycle-Delay", 250, 0, 500));
    protected final Setting<Integer> shootDelay =
        register(new NumberSetting<>("Shoot-Delay", 500, 0, 500));
    protected final Setting<Integer> minDura =
        register(new NumberSetting<>("Min-Potion", 0, 0, 1000));
    protected final Setting<Bind> cycleButton =
        register(new BindSetting("Cycle-Bind", Bind.none()));
    protected final Setting<Boolean> keyCycle =
        register(new BooleanSetting("Bind-Cycle-BlackListed", true));
    protected final Setting<Boolean> preCycle =
        register(new BooleanSetting("Fast-Cycle", false));
    protected final Setting<Boolean> fastCancel =
        register(new BooleanSetting("Fast-Cancel", false));

    protected final Set<Potion> cycled = new HashSet<>();
    protected final StopWatch cycleTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected boolean fast;

    public Arrows()
    {
        super("Arrows",
                Category.Player,
                "Add_Potion", "potion",
                SimpleRemovingSetting::new,
                s -> "Black/Whitelist " + s.getName() + " potion arrows.");
        super.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerKeyboard(this));
        this.setData(new SimpleData(this, "Shoot yourself with arrows. Not compatible with AntiPotion."));
    }

    @Override
    protected void onEnable()
    {
        fast = false;
    }

    @Override
    public String getInput(String input, boolean add)
    {
        if (add)
        {
            String potionName = getPotionNameStartingWith(input);
            if (potionName != null)
            {
                return TextUtil.substring(potionName, input.length());
            }

            return "";
        }

        return super.getInput(input, false);
    }

    @Override
    public String getDisplayInfo()
    {
        ItemStack stack = findArrow();
        if (!stack.isEmpty())
        {
            return Text.translatable(stack.getTranslationKey())
                    .getString()
                    .replace("Arrow of ", "")
                    .replace(" Arrow", "");
        }
        return null;
    }

    protected boolean badStack(ItemStack stack)
    {
        return badStack(stack, true, Collections.emptySet());
    }

    protected boolean badStack(ItemStack stack,
                               boolean checkType,
                               Set<Potion> cycled)
    {
        Potion type = PotionContentsComponent.getPotion(stack);
        if (stack.getItem() instanceof SpectralArrowItem)
        {
            type = SPECTRAL;
        }

        if (cycled.contains(type))
        {
            return true;
        }

        if (checkType)
        {
            if (BAD_TYPES.contains(type))
            {
                return true;
            }
        }
        else if (keyCycle.getValue()
                || type.getEffects().isEmpty() && isValid("none"))
        {
            return false;
        }

        if (stack.getItem() instanceof SpectralArrowItem)
        {
            return !isValid("Spectral") || mc.player.isGlowing();
        }

        boolean inValid = true;
        for (StatusEffectInstance e : type.getEffects())
        {
            if (!isValid(I18n.translate(e.getTranslationKey())))
            {
                return true;
            }

            StatusEffectInstance eff = mc.player.getActiveStatusEffects().get(e);
            if (eff == null || eff.getDuration() < minDura.getValue())
            {
                inValid = false;
            }
        }

        if (!checkType && !keyCycle.getValue())
        {
            return false;
        }

        return inValid;
    }

    /**
     *
     * @param recursive should be false when u call this.
     * @param key should be false if u cycle from the keyboard
     */
    public void cycle(boolean recursive, boolean key)
    {
        if (!InventoryUtil.validScreen()
            || key && !cycleTimer.passed(delay.getValue()))
        {
            return;
        }

        int firstSlot = -1;
        int secondSlot = -1;
        ItemStack arrow = null;
        if (isArrow(mc.player.getStackInHand(Hand.OFF_HAND)))
        {
            firstSlot = 45;
        }

        if (isArrow(mc.player.getStackInHand(Hand.MAIN_HAND)))
        {
            if (firstSlot == -1)
            {
                firstSlot = InventoryUtil.hotbarToInventory(
                        mc.player.getInventory().selectedSlot);
            }
            else if (!badStack(
                    mc.player.getStackInHand(Hand.MAIN_HAND), key, cycled))
            {
                secondSlot = InventoryUtil.hotbarToInventory(
                        mc.player.getInventory().selectedSlot);
                arrow = mc.player.getStackInHand(Hand.MAIN_HAND);
            }
        }

        /*
        if (!badStack(mc.player.getInventory().getItemStack(), key, cycled))
        {
            secondSlot = -2;
            arrow = mc.player.getInventory().getItemStack();
        }
        //TODO: fix
         */

        if (firstSlot == -1 || secondSlot == -1)
        {
            for (int i = 0; i < mc.player.getInventory().size(); i++)
            {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (!isArrow(stack))
                {
                    continue;
                }

                if (firstSlot == -1)
                {
                    firstSlot = InventoryUtil.hotbarToInventory(i);
                }
                else if (!badStack(stack, key, cycled))
                {
                    secondSlot = InventoryUtil.hotbarToInventory(i);
                    arrow = stack;
                    break;
                }
            }
        }

        if (firstSlot == -1)
        {
            return;
        }

        if (secondSlot == -1)
        {
            if (!recursive && !cycled.isEmpty())
            {
                cycled.clear();
                cycle(true, key);
            }

            return;
        }

        Potion type = PotionContentsComponent.getPotion(arrow);
        if (arrow.getItem() instanceof SpectralArrowItem)
        {
            type = SPECTRAL;
        }

        cycled.add(type);
        int finalFirstSlot  = firstSlot;
        int finalSecondSlot = secondSlot;
        Item inFirst  = InventoryUtil.get(finalFirstSlot).getItem();
        Item inSecond = InventoryUtil.get(finalSecondSlot).getItem();
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if (InventoryUtil.get(finalFirstSlot).getItem() == inFirst
                && InventoryUtil.get(finalSecondSlot).getItem() == inSecond)
            {
                if (finalSecondSlot == -2)
                {
                    InventoryUtil.click(finalFirstSlot);
                }
                else
                {
                    InventoryUtil.click(finalSecondSlot);
                    InventoryUtil.click(finalFirstSlot);
                    InventoryUtil.click(finalSecondSlot);
                }
            }
        });

        cycleTimer.reset();
    }

    protected ItemStack findArrow()
    {
        if (isArrow(mc.player.getStackInHand(Hand.OFF_HAND)))
        {
            return mc.player.getStackInHand(Hand.OFF_HAND);
        }
        else if (isArrow(mc.player.getStackInHand(Hand.MAIN_HAND)))
        {
            return mc.player.getStackInHand(Hand.MAIN_HAND);
        }

        for (int i = 0; i < mc.player.getInventory().size(); i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isArrow(stack))
            {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    protected boolean isArrow(ItemStack stack)
    {
        return stack.getItem() instanceof ArrowItem;
    }

    public static String getPotionNameStartingWith(String name)
    {
        Potion potion = getPotionStartingWith(name);
        if (potion == SpecialPot.SPECTRAL)
        {
            return "Spectral";
        }
        else if (potion == SpecialPot.NONE)
        {
            return "None";
        }

        if (potion != null)
        {
            return I18n.translate(potion.finishTranslationKey(""));
        }

        return null;
    }

    public static Potion getPotionStartingWith(String name)
    {
        if (name == null)
        {
            return null;
        }

        name = name.toLowerCase(); //TODO: fix
        /*
        for (Potion potion : Potion.REGISTRY)
        {
            if (I18n.translate(potion.finishTranslationKey("")).toLowerCase().startsWith(name))
            {
                return potion;
            }
        }

         */

        if ("spectral".startsWith(name))
        {
            return SpecialPot.SPECTRAL;
        }

        if ("none".startsWith(name))
        {
            return SpecialPot.NONE;
        }

        return null;
    }

    private static final class SpecialPot extends Potion
    {
        public static final SpecialPot SPECTRAL = new SpecialPot();
        public static final SpecialPot NONE     = new SpecialPot();

        private SpecialPot()
        {
            super();
        }
    }

}
