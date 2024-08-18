package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.util.IKeyBinding;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

final class ListenerMotion extends
        ModuleListener<ElytraFlight, MotionUpdateEvent> {
    private static final Random RANDOM = new Random();
    private static float previousTimerVal = -1.0f;

    public ListenerMotion(ElytraFlight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() != Stage.PRE) {
            return;
        }

        ItemStack stack =
                mc.player.getEquippedStack(EquipmentSlot.CHEST);

        if (stack.getItem() != Items.ELYTRA || !ElytraItem.isUsable(stack)) {
            return;
        }

        if (mc.player.isFallFlying()
                && (module.noWater.getValue() && mc.player.isInFluid()
                || module.noGround.getValue() && mc.player.onGround)) {
            module.sendFallPacket();
            return;
        }

        if (mc.player.isFallFlying()) {
            if (module.mode.getValue() != ElytraMode.Boost && MovementUtil.anyMovementKeys()) {
                float moveStrafe = mc.player.sidewaysSpeed,
                        moveForward = mc.player.forwardSpeed;
                float strafe = moveStrafe * 90 * (moveForward != 0 ? 0.5f : 1);
                event.setYaw(MathHelper.wrapDegrees(mc.player.yaw - strafe - (moveForward < 0 ? 180 : 0)));
            }
            if (module.customPitch.getValue()) {
                event.setPitch(module.pitch.getValue().floatValue());
            }
            if (module.rockets.getValue() && module.mode.getValue() != ElytraMode.Packet) {
                if (module.rocketTimer.passed(module.rocketDelay.getValue() * 1000)) {
                    int slot = InventoryUtil.findHotbarItem(Items.FIREWORK_ROCKET);
                    if (slot != -1) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                            int last = mc.player.getInventory().selectedSlot;
                            Hand hand = InventoryUtil.getHand(slot);

                            InventoryUtil.switchTo(slot);

                            NetworkUtil.sendSequenced(seq -> new PlayerInteractItemC2SPacket(hand, seq, event.getYaw(), event.getPitch()));
                            mc.player.swingHand(hand);

                            if (module.rocketSwitchBack.getValue()) {
                                InventoryUtil.switchTo(last);
                            }
                        });
                    }
                    module.rocketTimer.reset();
                }
            }
        }

        if (module.mode.getValue() == ElytraMode.Packet) {
            boolean falling = false;
            if (module.infDura.getValue() || !mc.player.isFallFlying()) {
                module.sendFallPacket();
                falling = true;
            }

            if (module.ncp.getValue()
                    && !module.lag
                    && (Math.abs(event.getX()) >= 0.05
                    || Math.abs(event.getZ()) >= 0.05)) {
                double y = 1.0E-8 + 1.0E-8 * (1.0 + RANDOM.nextInt(
                        1 + (RANDOM.nextBoolean()
                                ? RANDOM.nextInt(34)
                                : RANDOM.nextInt(43))));

                if (mc.player.onGround || mc.player.age % 2 == 0) {
                    event.setY(event.getY() + y);
                    return;
                }

                event.setY(event.getY() - y);
                return;
            }

            if (falling) {
                return;
            }
        }

        if (module.autoStart.getValue()
                && mc.options.jumpKey.isPressed()
                && !mc.player.isFallFlying()
                && mc.player.getVelocity().y < 0) {
            if (previousTimerVal == -1.0f) {
                previousTimerVal = Managers.TIMER.getSpeed();
            }
            Managers.TIMER.setTimer(0.17f);
            if (module.timer.passed(10)) {
                ((IKeyBinding) mc.options.jumpKey).earthhack$setPressed(true);
                module.sendFallPacket();
                module.timer.reset();
            } else {
                ((IKeyBinding) mc.options.jumpKey).earthhack$setPressed(false);
            }
            return;
        } else {
            if (previousTimerVal != -1.0f) {
                Managers.TIMER.setTimer(previousTimerVal);
                previousTimerVal = -1.0f;
            }
        }

        if (module.infDura.getValue() && mc.player.isFallFlying()) {
            module.sendFallPacket();
        }
    }

}