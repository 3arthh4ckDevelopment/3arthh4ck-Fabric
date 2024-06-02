package me.earth.earthhack.impl.modules.misc.middleclick;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.events.keyboard.MouseEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

public class MiddleClick extends Module {

    protected final Setting<Bind> keyBind =
            register(new BindSetting("KeyBind", Bind.none()));
    protected final Setting<Boolean> pickBlock =
            register(new BooleanSetting("PickBlock", true));
    protected final Setting<Boolean> cancelPickBlock =
            register(new BooleanSetting("CancelPickBlock", true));
    protected final Setting<Boolean> entities =
            register(new BooleanSetting("FriendEntities", true));
    protected final Setting<Boolean> air =
            register(new BooleanSetting("AirPearl", true));
    protected final Setting<PressType> bindMode =
            register(new EnumSetting<>("BindMode", PressType.Press))
                    .setComplexity(Complexity.Expert);

    protected enum PressType {
        Press, Release
    }

    private boolean isButtonPressed = false;

    public MiddleClick() {
        super("MCFRewrite", Category.Misc);

        this.listeners.add(new LambdaListener<>(ClickMiddleEvent.class, e -> e.setCancelled(cancelPickBlock.getValue())));

        this.listeners.add(new LambdaListener<>(KeyboardEvent.class, e -> {
            if (e.getKey() == keyBind.getValue().getKey() && e.getEventState() == bindMode.getValue().equals(PressType.Press)) {
                onClick();
            }
        }));

        this.listeners.add(new LambdaListener<>(MouseEvent.class, e -> {
            if (e.getState() == bindMode.getValue().equals(PressType.Press)) {
                onClick();
            }
        }));

        this.setData(new MiddleClickData(this));
    }

    @Override
    public void onPreTick() {
        if (mc.currentScreen == null) {
            long windowHandle = mc.getWindow().getHandle();
            int mouseButtonState = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_MIDDLE);

            if (mouseButtonState == GLFW.GLFW_PRESS) {
                if (!isButtonPressed) {
                    isButtonPressed = true;
                    onClick();
                }
            } else {
                isButtonPressed = false;
            }
        }
    }

    private void onClick() {
        if (mc.player != null && mc.world != null && mc.crosshairTarget != null) {
            HitResult result = mc.crosshairTarget;

            switch (result.getType()) {
                case ENTITY:
                    Entity entity = mc.targetedEntity;
                    if (entities.getValue() && entity instanceof PlayerEntity) {
                        if (Managers.FRIENDS.contains((PlayerEntity) entity)) {
                            Managers.FRIENDS.remove(entity);
                            Managers.CHAT.sendDeleteMessage(
                                    TextColor.RED + entity.getName()
                                            + " unfriended.",
                                    entity.getName().getString(), ChatIDs.FRIEND);
                        } else {
                            GameProfile profile =
                                    ((PlayerEntity) entity).getGameProfile();
                            Managers.FRIENDS.add(
                                    profile.getName(), profile.getId());

                            Managers.CHAT.sendDeleteMessage(
                                    TextColor.AQUA + entity.getName()
                                            + " friended.",
                                    entity.getName().getString(), ChatIDs.FRIEND);
                        }
                    }
                    break;
                case MISS:
                    if (air.getValue() && result.getType() == HitResult.Type.MISS) {
                        int pearlSlot = findPearlInHotbar();
                        if (pearlSlot != -1) {
                            ChatUtil.sendMessage(Formatting.GREEN + "Throwing Ender Pearl....");
                            int oldSlot = mc.player.getInventory().selectedSlot;
                            mc.player.getInventory().selectedSlot = pearlSlot;
                            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                            mc.player.getInventory().selectedSlot = oldSlot;
                        } else {
                            ChatUtil.sendMessage(Formatting.RED + "No Ender Pearl found!");
                        }
                    }
                    break;
            }
        }
    }

    private boolean isItemStackPearl(ItemStack itemStack) {
        return itemStack.getItem() instanceof EnderPearlItem;
    }

    private int findPearlInHotbar() {
        for (int index = 0; index < 9; index++) {
            if (isItemStackPearl(mc.player.getInventory().getStack(index))) {
                return index;
            }
        }
        return -1;
    }
}
