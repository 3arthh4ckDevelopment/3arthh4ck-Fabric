package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.effect.StatusEffects;

final class ListenerMotion extends ModuleListener<Flight, MotionUpdateEvent> {
    private static final ModuleCache<NoSlowDown> NO_SLOW_DOWN =
            Caches.getModule(NoSlowDown.class);
    private static final SettingCache<Boolean, BooleanSetting, NoSlowDown> GUI =
            Caches.getSetting(NoSlowDown.class, BooleanSetting.class, "GuiMove", true);

    public ListenerMotion(Flight module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        switch (module.mode.getValue()) {
            case ConstantiamNew:
                if (event.getStage() == Stage.PRE) {
                    // NetworkUtil.send(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    if (module.constNewStage > 2) {
                        mc.player.getVelocity().y = 0;
                        mc.player.setPosition(mc.player.getX(), mc.player.getY() - 0.032, mc.player.getZ());
                        ++module.constNewTicks;
                        switch (module.constNewTicks) {
                            case 1:
                                module.constY *= -0.949999988079071D;
                                break;
                            case 2:
                            case 3:
                            case 4:
                                module.constY += 3.25E-4D;
                                break;
                            case 5:
                                module.constY += 5.0E-4D;
                                module.constNewTicks = 0;
                        }
                        event.setY(mc.player.getY() + module.constY);
                    }
                } else if (module.constNewStage > 2) {
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() + 0.032, mc.player.getZ());
                }
                if (!mc.player.onGround
                        && !mc.player.verticalCollision
                        && mc.player.age % 30 == 0) {
                    event.setY(event.getY() - 0.032);
                    // mc.player.setPosition(mc.player.getX(), mc.player.getY() - 0.032, mc.player.getZ());
                }
                break;
            case ConstoHare:
            case ConstoHareFast:
                if (event.getStage() == Stage.PRE) {
                    ++module.oHareCounter;
                    if (mc.player.forwardSpeed == 0
                            && mc.player.sidewaysSpeed == 0) {
                        mc.player.setPosition(mc.player.getX() + 1.0D,
                                mc.player.getY() + 1.0D,
                                mc.player.getZ() + 1.0D);
                        mc.player.setPosition(mc.player.prevX,
                                mc.player.prevY, mc.player.prevZ);
                        mc.player.getVelocity().x = 0.0D;
                        mc.player.getVelocity().z = 0.0D;
                    }
                    mc.player.getVelocity().y = 0.0D;
                    if (mc.options.jumpKey.isPressed())
                        mc.player.getVelocity().y += 0.5f;
                    if (mc.options.sneakKey.isPressed())
                        mc.player.getVelocity().y -= 0.5f;
                    if (module.oHareCounter == 2) {
                        mc.player.setPosition(mc.player.getX(),
                                mc.player.getY() + 1.0E-10D,
                                mc.player.getZ());
                        module.oHareCounter = 0;
                    }
                } else {
                    double xDist = mc.player.getX() - mc.player.prevX;
                    double zDist = mc.player.getZ() - mc.player.prevZ;
                    module.oHareLastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                }
                break;
            case Constantiam:
            case Normal:
                mc.player.getVelocity().x = 0.0;
                mc.player.getVelocity().y = 0.0;
                mc.player.getVelocity().z = 0.0;

                if (module.glide.getValue()) {
                    mc.player.getVelocity().y -= module.glideSpeed.getValue();
                }

                if (!mc.isWindowFocused()
                        && (!NO_SLOW_DOWN.isEnabled() || !GUI.getValue())) {
                    break;
                }

                if (mc.player.input.sneaking) {
                    mc.player.getVelocity().y += 0.4000000059604645;
                }

                if (mc.player.input.sneaking) {
                    mc.player.getVelocity().y -= 0.4000000059604645;
                }

                if (module.mode.getValue() == FlightMode.Constantiam
                        && !mc.player.onGround
                        && !mc.player.verticalCollision
                        && mc.player.age % 20 == 0
                        && module.antiKick.getValue()) {
                    mc.player.setPosition(mc.player.getX(), mc.player.getY() - 0.032, mc.player.getZ());
                    // module.clipped = true;
                }

                break;
            case Jump:
                if (event.getStage() == Stage.PRE) {
                    if (!mc.player.onGround) {
                        if (!mc.player.input.jumping) {
                            if ((!MovementUtil.noMovementKeys())
                                    && !mc.player.input.sneaking) {
                                module.counter++;
                                if (module.counter >= 11) {
                                    //mc.player.jumpMovementFactor = 0.7F;
                                    mc.player.jump();
                                    module.counter = 0;
                                }
                            }
                        } else if (!mc.player.input.sneaking) {
                            module.counter++;
                            if (module.counter >= 4) {
                                //mc.player.jumpMovementFactor = 0.01f;
                                mc.player.jump();
                                module.counter = 0;
                            }
                        }
                    }
                }
        }

        if (event.getStage() == Stage.PRE) {
            module.constNewOffset = mc.player.getX() - mc.player.prevX;
            double zDif = mc.player.getZ() - mc.player.prevZ;
            module.lastDist = Math.sqrt(module.constNewOffset * module.constNewOffset + zDif * zDif);
        }

        if (module.antiKick.getValue()
                && !(module.mode.getValue() == FlightMode.Constantiam)) {
            module.antiCounter++;
            if (module.antiCounter >= 12
                    && !mc.player.getActiveStatusEffects().containsKey(StatusEffects.LEVITATION)
                    && !mc.player.isFallFlying()
                    && mc.world.getEntityCollisions(mc.player,
                            mc.player
                                    .getBoundingBox()
                                    .expand(0.0625)
                                    .expand(0.0, -0.55, 0.0))
                    .isEmpty()) {
                event.setY(event.getY() - 0.03126);
                // event.setOnGround(true);
                if (module.antiCounter >= 22) {
                    module.antiCounter = 0;
                }
            }
        }
    }

}