package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.entity.effect.StatusEffects;

final class ListenerMove extends ModuleListener<Flight, MoveEvent> {
    public ListenerMove(Flight module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;

        switch (module.mode.getValue()) {
            case ConstantiamNew:
                if (MovementUtil.isMoving()) {
                    switch (module.constNewStage) {
                        case 0:
                            if (mc.player.onGround
                                    && mc.player.verticalCollision) {
                                module.constMovementSpeed = 0.5D * module.speed.getValue();
                            }
                            break;
                        case 1:
                            if (mc.player.onGround
                                    && mc.player.verticalCollision) {
                                double y = 0.4;
                                mc.player.getVelocity().y = y;
                                event.setY(y);
                            }
                            module.constMovementSpeed *= 2.149;
                            break;
                        case 2:
                            module.constMovementSpeed = 1.3D * module.speed.getValue();
                            break;
                        default:
                            module.constMovementSpeed = module.lastDist - module.lastDist / 159.0D;
                    }
                    MovementUtil.strafe(event, Math.max(module.constMovementSpeed, MovementUtil.getSpeed()));
                    ++module.constNewStage;
                }
                break;
            case ConstoHareFast:
                if (forward == 0.0F && strafe == 0.0F) {
                    event.setX(0.0D);
                    event.setZ(0.0D);
                }
                if (module.oHareLevel != 1 || mc.player.forwardSpeed == 0.0F
                        && mc.player.sidewaysSpeed == 0.0F) {
                    if (module.oHareLevel == 2) {
                        module.oHareLevel = 3;
                        module.oHareMoveSpeed *= 2.1499999D;
                    } else if (module.oHareLevel == 3) {
                        module.oHareLevel = 4;
                        double difference = (mc.player.age % 2 == 0 ? 0.0103D : 0.0123D)
                                * (module.oHareLastDist - MovementUtil.getSpeed());
                        module.oHareMoveSpeed = module.oHareLastDist - difference;
                    } else {
                        if (!mc.world
                                .getEntityCollisions(mc.player,
                                        mc.player.getBoundingBox().offset(0.0D,
                                                mc.player.getVelocity().y, 0.0D)).isEmpty() || mc.player.verticalCollision) {
                            module.oHareLevel = 1;
                        }
                        module.oHareMoveSpeed = module.oHareLastDist - module.oHareLastDist / 159.0D;
                    }
                } else {
                    module.oHareLevel = 2;
                    double boost = mc.player.getActiveStatusEffects().containsKey(StatusEffects.SPEED) ? 1.56 : 2.034;
                    module.oHareMoveSpeed = boost * MovementUtil.getSpeed();
                }
                module.oHareMoveSpeed = Math.max(module.oHareMoveSpeed, MovementUtil.getSpeed());
                MovementUtil.strafe(event, Math.max(module.oHareMoveSpeed, MovementUtil.getSpeed()));
                break;
            case Constantiam:
                event.setX(event.getX() * module.speed.getValue());
                event.setZ(event.getZ() * module.speed.getValue());
                /*if (mc.player.onGround && module.constantiamStage == 0)
                {
                    event.setY(0.4);
                    module.constantiamStage++;
                    break;
                }*/
                if (mc.player.age % 2 == 0) {
                    event.setY(0.00118212);
                } else {
                    event.setY(-0.00118212);
                }
                ++module.constantiamStage;
                break;
            case Normal:
                event.setX(event.getX() * module.speed.getValue());
                event.setZ(event.getZ() * module.speed.getValue());
                break;
            case AAC:
                if (!mc.player.onGround && !PositionUtil.inLiquid()) {
                    MovementUtil.strafe(event, 0.4521096646785736);
                }
                break;
            case Creative:
                double speed = module.speed.getValue() / 10.0;
                if (mc.player.input.jumping) {
                    event.setY(speed);
                    mc.player.getVelocity().y = speed;
                } else if (mc.player.input.sneaking) {
                    event.setY(-speed);
                    mc.player.getVelocity().y = -speed;
                } else {
                    event.setY(0);
                    mc.player.getVelocity().y = 0;
                    if (!mc.player.verticalCollision && module.glide.getValue()) {
                        mc.player.getVelocity().y -= module.glideSpeed.getValue();
                        event.setY(mc.player.getVelocity().y);
                    }
                }

                MovementUtil.strafe(event, speed);
                break;
        }
    }

}