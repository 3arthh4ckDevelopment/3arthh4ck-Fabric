package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

final class ListenerMove extends ModuleListener<ElytraFlight, MoveEvent> {
    public ListenerMove(ElytraFlight module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        ItemStack stack = mc.player
                .getEquippedStack(EquipmentSlot.CHEST);

        if (stack.getItem() == Items.ELYTRA && ElytraItem.isUsable(stack)) {
            switch (module.mode.getValue()) {
                case Wasp:
                    if (!mc.player.isFallFlying()) {
                        return;
                    }

                    double vSpeed = mc.options.jumpKey.isPressed()
                            ? module.vSpeed.getValue()
                            : mc.options.sneakKey.isPressed()
                            ? -module.vSpeed.getValue()
                            : 0;

                    event.setY(vSpeed);
                    mc.player.setVelocity(0, 0, 0);
                    mc.player.getVelocity().y = vSpeed;
                    mc.player.speed = (float) vSpeed;

                    if (MovementUtil.noMovementKeys()
                            && !mc.options.jumpKey.isPressed()
                            && !mc.options.sneakKey.isPressed()) {

                        event.setX(0);
                        event.setY(0);
                        event.setY(module.antiKick.getValue() ? -module.glide.getValue() : 0);
                        return;
                    }

                    MovementUtil.strafe(event, module.hSpeed.getValue());
                    break;
                case Packet:
                    if (!mc.player.onGround || !module.noGround.getValue()) {
                        if (module.accel.getValue()) {
                            if (module.lag) {
                                module.speed = 1.0;
                                module.lag = false;
                            }

                            if (module.speed < module.hSpeed.getValue()) {
                                module.speed += 0.1;
                            }

                            if (module.speed - 0.1D > module.hSpeed.getValue()) {
                                module.speed -= 0.1;
                            }
                        } else {
                            module.speed = module.hSpeed.getValue();
                        }

                        if (!MovementUtil.anyMovementKeys()
                                && !mc.player.collidedSoftly
                                && module.antiKick.getValue()) {
                            if (module.timer.passed(1000)) {
                                module.lag = true;
                                mc.player.getVelocity().x += 0.03
                                        * Math.sin(Math.toRadians(++module.kick * 4));
                                mc.player.getVelocity().z += 0.03
                                        * Math.cos(Math.toRadians(module.kick * 4));
                            }
                        } else {
                            module.timer.reset();
                            module.lag = false;
                        }

                        if (module.vertical.getValue()
                                && mc.player.input.jumping) {
                            mc.player.getVelocity().y = module.vSpeed.getValue();
                            event.setY(module.vSpeed.getValue());
                        } else if (mc.player.input.sneaking) {
                            mc.player.getVelocity().y = -module.vSpeed.getValue();
                            event.setY(-module.vSpeed.getValue());
                        } else if (module.ncp.getValue()) {
                            if (mc.player.age % 32 != 0
                                    || module.lag
                                    || !(Math.abs(event.getX()) >= 0.05D)
                                    && !(Math.abs(event.getZ()) >= 0.05D)) {
                                mc.player.getVelocity().y = -2.0E-4;
                                event.setY(-2.0E-4);
                            } else {
                                module.speed = module.speed - module.speed / 2.0 * 0.1;
                                mc.player.getVelocity().y = -2.0E-4D;
                                event.setY(0.006200000000000001);
                            }
                        } else {
                            mc.player.getVelocity().y = 0.0;
                            event.setY(0.0);
                        }

                        event.setX(event.getX() * (module.lag ? 0.5 : module.speed));
                        event.setZ(event.getZ() * (module.lag ? 0.5 : module.speed));
                    }

                    break;
                case Boost:
                    if (mc.player.isFallFlying()
                            && module.noWater.getValue()
                            && mc.player.isInFluid()) {
                        return;
                    }

                    if (mc.player.input.jumping
                            && mc.player.isFallFlying()) {
                        float yaw = mc.player.yaw * 0.017453292f;
                        mc.player.getVelocity().x -= MathHelper.sin(yaw) * 0.15f;
                        mc.player.getVelocity().y += MathHelper.cos(yaw) * 0.15f;
                    }

                    break;
                case Control:
                    if (mc.player.isFallFlying()) {
                        if (!mc.player.input.pressingForward
                                && !mc.player.input.sneaking) {
                            mc.player.setVelocity(0.0, 0.0, 0.0);
                        } else if (mc.player.input.pressingForward
                                && (module.vertical.getValue()
                                || mc.player.prevPitch > 0.0F)) {
                            float yaw = (float) Math.toRadians(mc.player.yaw);
                            double speed = module.hSpeed.getValue() / 10.0;
                            mc.player.getVelocity().x = MathHelper.sin(yaw) * -speed;
                            mc.player.getVelocity().z = MathHelper.cos(yaw) * speed;
                        }
                    }

                    break;
                case Normal:
                    if (mc.player.isFallFlying()
                            && module.noWater.getValue()
                            && mc.player.isInFluid()) {
                        return;
                    }

                    if (mc.player.input.jumping
                            || !mc.isWindowFocused()
                            && mc.player.isFallFlying()) {
                        event.setY(0.0);
                    }

                    if (mc.isWindowFocused()
                            && module.instant.getValue()
                            && mc.player.input.jumping
                            && !mc.player.isFallFlying()
                            && module.timer.passed(1000)) {
                        mc.player.setJumping(false);
                        mc.player.setSprinting(true);
                        mc.player.jump();
                        module.sendFallPacket();
                        module.timer.reset();
                        return;
                    }

                    break;
                default:
            }
        }
    }

}