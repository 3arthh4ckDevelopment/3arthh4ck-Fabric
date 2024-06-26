package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings({"ConstantConditions", "unused"})
public class InventoryUtil implements Globals
{
    // deprecated. should be changed
    public static final ItemStack ILLEGAL_STACK =
            new ItemStack(Item.fromBlock(Blocks.BEDROCK));

    public static void switchTo(int slot)
    {
        if (mc.player.getInventory().selectedSlot != slot && slot > -1 && slot < 9)
        {
            mc.player.getInventory().selectedSlot = slot;
            syncItem();
        }
    }

    public static void switchToBypass(int slot)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if (mc.player.getInventory().selectedSlot != slot
                    && slot > -1 && slot < 9)
            {
                int lastSlot = mc.player.getInventory().selectedSlot;
                int targetSlot = hotbarToInventory(slot);
                int currentSlot = hotbarToInventory(lastSlot);
                mc.interactionManager
                        .clickSlot(0, targetSlot, 0, SlotActionType.PICKUP,
                                mc.player);
                mc.interactionManager
                        .clickSlot(0, currentSlot, 0, SlotActionType.PICKUP,
                                mc.player);
                mc.interactionManager
                        .clickSlot(0, targetSlot, 0, SlotActionType.PICKUP,
                                mc.player);
            }
        });
    }

    /**
     * Bypasses NCP item switch cooldown
     * @param slot INVENTORY SLOT (NOT HOTBAR) to switch to
     */
    public static void switchToBypassAlt(int slot)
    {
        if (slot != -1) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                    mc.interactionManager
                            .clickSlot(0, slot, mc.player.getInventory().selectedSlot,
                                    SlotActionType.SWAP, mc.player));
        }
    }

    /**
     * Yet Another Cooldown Bypass... best one yet by far.
     * @param slot hotbar slot to switch to.
     */
    public static void bypassSwitch(int slot)
    {
        if (slot >= 0)
        {
            mc.interactionManager.pickFromInventory(slot);
        }
    }

    /**
     * {@see https://wiki.vg/index.php?title=Protocol&oldid=14204#Click_Window/}
     */
    public static void illegalSync()
    {
        if (mc.player != null)
        {
            PacketUtil.click(
                    0, 0, 0, 0, SlotActionType.PICKUP, ILLEGAL_STACK);
        }
    }

    public static int findHotbarBlock(Block block, Block...optional)
    {
        return findInHotbar(s -> ItemUtil.areSame(s, block),
                CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findHotbarItem(Item item, Item...optional)
    {
        return findInHotbar(s -> ItemUtil.areSame(s, item),
                CollectionUtil.convert(optional, o -> s -> ItemUtil.areSame(s, o)));
    }

    public static int findInHotbar(Predicate<ItemStack> condition)
    {
        return findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition,
                                   boolean offhand)
    {
        if (offhand && condition.test(mc.player.getOffHandStack()))
        {
            return -2;
        }

        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            if (condition.test(mc.player.getInventory().getStack(i)))
            {
                result = i;
                if (mc.player.getInventory().selectedSlot == i)
                {
                    break;
                }
            }
        }

        return result;
    }

    public static int findInInventory(Predicate<ItemStack> condition,
                                      boolean xCarry)
    {
        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = mc.player
                    .getInventory()
                    .getStack(i);

            if (condition.test(stack))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = mc.player
                        .getInventory()
                        .getStack(i);

                if (condition.test(stack))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findInCraftingTable(Inventory container,
                                          Predicate<ItemStack> condition)
    {
        for (int i = 11; i < 47; i++)
        {
            ItemStack stack = container
                    .getStack(i);
            if (condition.test(stack))
            {
                return i;
            }
        }

        return -1;
    }

    public static int findInHotbar(Predicate<ItemStack> condition,
                                   Iterable<Predicate<ItemStack>> optional)
    {
        int result = findInHotbar(condition);
        if (result == -1)
        {
            for (Predicate<ItemStack> opt : optional)
            {
                result = findInHotbar(opt);
                if (result != -1)
                {
                    break;
                }
            }
        }

        return result;
    }

    public static int findBlock(Block block, boolean xCarry)
    {
        if (ItemUtil.areSame(mc.player.getInventory().getMainHandStack(), block))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = mc.player
                    .getInventory()
                    .getStack(i);

            if (ItemUtil.areSame(stack, block))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = mc.player
                        .getInventory()
                        .getStack(i);

                if (ItemUtil.areSame(stack, block))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int findItem(Item item, Inventory container)
    {
        for (int i = 0; i < container.size(); i++)
        {
            ItemStack stack = container.getStack(i);

            if (stack.getItem() == item)
            {
                return i;
            }
        }

        return -1;
    }

    public static int findItem(Item item, boolean xCarry)
    {
        return findItem(item, xCarry, Collections.emptySet());
    }

    public static int findItem(Item item, boolean xCarry, Set<Integer> ignore)
    {
        if (mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getItem() == item
                && !ignore.contains(-2))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            if (ignore.contains(i))
            {
                continue;
            }

            if (get(i).getItem() == item)
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                if (ignore.contains(i))
                {
                    continue;
                }

                if (get(i).getItem() == item)
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getCount(Item item)
    {
        int result = 0;
        for (int i = 0; i < 46; i++)
        {
            ItemStack stack = mc.player
                    .getInventory()
                    .getStack(i);

            if (stack.getItem() == item)
            {
                result += stack.getCount();
            }
        }

        if (mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getItem() == item)
        {
            result += mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getCount();
        }

        return result;
    }

    public static boolean isHoldingServer(Item item)
    {
        ItemStack offHand  = mc.player.getOffHandStack();
        if (ItemUtil.areSame(offHand, item))
        {
            return true;
        }

        ItemStack mainHand = mc.player.getMainHandStack();
        if (ItemUtil.areSame(mainHand, item))
        {
            int current = mc.player.getInventory().selectedSlot;
            int server  = getServerItem();
            return server == current;
        }

        return false;
    }

    public static boolean isHolding(Class<?> clazz)
    {
        return clazz.isAssignableFrom(
                mc.player.getMainHandStack().getItem().getClass())
                || clazz.isAssignableFrom(
                mc.player.getOffHandStack().getItem().getClass());
    }

    public static boolean isHolding(Item item)
    {
        return isHolding(mc.player, item);
    }

    public static boolean isHolding(Block block)
    {
        return isHolding(mc.player, block);
    }

    public static boolean isHolding(Entity entity, Item item)
    {
        ItemStack mainHand = entity.getHandItems().iterator().next().copyAndEmpty();
        ItemStack offHand  = entity.getHandItems().iterator().next().copyAndEmpty();

        return ItemUtil.areSame(mainHand, item)
                || ItemUtil.areSame(offHand, item);
    }

    public static boolean isHolding(Entity entity, Block block)
    {
        ItemStack mainHand = entity.getHandItems().iterator().next().copyAndEmpty();
        ItemStack offHand  = entity.getHandItems().iterator().next().copyAndEmpty();

        return ItemUtil.areSame(mainHand, block)
                || ItemUtil.areSame(offHand, block);
    }

    /**
     * Returns {@link Hand#OFF_HAND} if the given
     * slot is -2 otherwise {@link Hand#MAIN_HAND}.
     *
     * @return the Hand for the given slot.
     */
    public static Hand getHand(int slot)
    {
        return slot == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    public static Hand getHand(Item item)
    {
        return mc.player.getMainHandStack().getItem() == item
                ? Hand.MAIN_HAND
                : mc.player.getOffHandStack().getItem() == item
                ? Hand.OFF_HAND
                : null;
    }

    /**
     * @return <tt>true</tt> if {@link MinecraftClient#currentScreen}
     *          is a screen on which we can clickSlot all
     *          Slots of the Inventory (with Armor and Offhand).
     */
    public static boolean validScreen()
    {
        return !(mc.currentScreen instanceof GenericContainerScreen)
                || mc.currentScreen instanceof InventoryScreen;
    }

    public static int getServerItem()
    {
        return ((IClientPlayerInteractionManager) mc.interactionManager).earthhack$getItem();
    }

    /**
     * Syncs the selectedSlot with the Server.
     * Should always be called while the
     * {@link Locks#PLACE_SWITCH_LOCK} is locked.
     */
    public static void syncItem()
    {
        ((IClientPlayerInteractionManager) mc.interactionManager).earthhack$syncItem();
    }

    /**
     * Calls {@link net.minecraft.client.network.ClientPlayerInteractionManager#
     * clickSlot(int, int, int, SlotActionType, PlayerEntity)}
     * for the arguments:
     * <p>-0
     * <p>-the given slot.
     * <p>-0
     * <p>-{@link SlotActionType#PICKUP}
     * <p>-mc.player
     *
     * @param slot the slot to click.
     */
    public static void click(int slot)
    {
        mc.interactionManager
                .clickSlot(0, slot, 0, SlotActionType.PICKUP, mc.player);
    }

    /**
     * @param slot the slot to get.
     * @return {@link Inventory()#get(int)} for the
     *          {@link MinecraftClient#player}'s getInventory() Container.
     */ // TODO: ensure that this is used everywhere where needed
    public static ItemStack get(int slot)
    {
         if (slot == -2)
         {
             return mc.player.getOffHandStack();
         }
        return mc.player.getInventory().getStack(slot);
    }

    public static PlayerInventory getInventory()
    {
        return mc.player.getInventory();
    }

    public static void put(int slot, ItemStack stack)
    {
        if (slot == -2)
        {
            mc.player.getInventory().setStack(slot, stack);
        }

        mc.player.getInventory().setStack(slot, stack);

        int invSlot = containerToSlots(slot);
        if (invSlot != -1) {
            mc.player.getInventory().setStack(invSlot, stack);
        }
    }

    public static int findEmptyHotbarSlot()
    {
        int result = -1;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR)
            {
                result = i;
            }
        }

        return result;
    }

    /**
     * Converts a HotbarSlot (for example retrieved by
     * {@link InventoryUtil#findHotbarBlock(Block, Block...)}),
     * to an getInventory() slot. -2 will be converted to the offhand
     * slot 45. If the slot doesn't belong to the hotbar it will
     * be returned unchanged.
     *
     * @param slot the slot to convert;
     * @return the slot as an getInventory() slot.
     */
    public static int hotbarToInventory(int slot)
    {
        if (slot == -2)
        {
            return 45;
        }

        if (slot > -1 && slot < 9)
        {
            return 36 + slot;
        }

        return slot;
    }

    /**
     * Returns <tt>true</tt> if the second stack can be
     * placed into the first one. In order for that to happen
     * the first stack needs to be:
     * <p>-empty</p>
     * <p>or</p>
     * <p>-have the same item as the second stack.</p>
     * <p>-have a maxStackSize > 1.</p>
     * <p>-No Subtypes, or the same MetaData as the second stack.</p>
     * <p>-{@link ItemStack#areEqual(ItemStack, ItemStack)}</p>
     *
     * @param inSlot the stack in the slot
     * @param stack the stack placed inside inSlot.
     * @return <tt>true</tt> if the stacks fit inside each other.
     */
    public static boolean canStack(ItemStack inSlot, ItemStack stack)
    {
        return inSlot.isEmpty()
                || inSlot.getItem() == stack.getItem()
                && inSlot.isStackable()
                && (!inSlot.hasNbt()
                || inSlot.getNbt() == stack.getNbt())
                && ItemStack.areItemsEqual(inSlot, stack);
    }

    /**
     * Checks if 2 ItemStacks are equal and ignores the durability.
     * Returns <tt>true</tt> if both are null as well.
     *
     * @param stack1 first stack
     * @param stack2 second stack
     * @return <tt>true</tt> if the stacks are equal.
     */
    public static boolean equals(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == null)
        {
            return stack2 == null;
        }
        else if (stack2 == null)
        {
            return false;
        }

        boolean empty1 = stack1.isEmpty();
        boolean empty2 = stack2.isEmpty();

        return empty1 == empty2
                && stack1.getName().equals(stack2.getName())
                && stack1.getItem() == stack1.getItem()
                && stack1.getNbt() == stack2.getNbt()
                && ItemStack.areItemsEqual(stack1, stack2);
    }

    /**
     * Locks the {@link Locks#WINDOW_CLICK_LOCK} and
     * performs a (or 2) clickSlot(s) with windowId 0 and type Pickup.
     * If the first slot is < 0 it won't be clicked.
     *
     * @param slot the slot to click first. (-2 if drag) (-1 if ignore)
     * @param to the slot to click second.
     * @param inSlot the item in the slot which is clicked first.
     * @param inTo the item in the slot which is clicked second.
     */
    public static void clickLocked(int slot, int to, Item inSlot, Item inTo)
    {
        Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
        {
            if ((slot == -1 || get(slot).getItem() == inSlot)
                    && get(to).getItem() == inTo)
            {
                boolean multi = slot >= 0;
                if (multi)
                {
                    Managers.NCP.startMultiClick();
                    click(slot);
                }

                click(to);

                if (multi)
                {
                    Managers.NCP.releaseMultiClick();
                }
            }
        });
    }

    /**
     * Converts slots from {@link } to slots from
     * {@link PlayerInventory}. Crafting slots are not part of the
     * {@link PlayerInventory}! For those slots -1 will be returned.
     *
     * @param containerSlot the slot in the container.
     * @return the slot in the Inventory or -1 if not in Inventory.
     */
    public static int containerToSlots(int containerSlot) {
        if (containerSlot < 5 || containerSlot > 45) { // crafting slots
            return -1;
        }

        if (containerSlot <= 9) {
            return 44 - containerSlot;
        }

        if (containerSlot < 36) {
            return containerSlot;
        }

        if (containerSlot < 45) {
            return containerSlot - 36;
        }

        return 40; // offhand is 40 here
    }

}
