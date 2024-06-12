package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<Phase, MotionUpdateEvent>
{
    private static final double[] off =
            {
                    -0.02500000037252903,
                    -0.028571428997176036,
                    -0.033333333830038704,
                    -0.04000000059604645,
                    -0.05000000074505806,
                    -0.06666666766007741,
                    -0.10000000149011612,
                    -0.20000000298023224,
                    -0.04000000059604645,
                    -0.033333333830038704,
                    -0.028571428997176036,
                    -0.02500000037252903
            };

    private final StopWatch timer = new StopWatch();

    public ListenerMotion(Phase module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        double xSpeed =
                mc.player.getHorizontalFacing().getVector().getX() * 0.1;
        double zSpeed =
                mc.player.getHorizontalFacing().getVector().getZ() * 0.1;

        switch (module.mode.getValue())
        {
            case Constantiam:
                if (event.getStage() == Stage.PRE
                        && mc.player.horizontalCollision
                        && !module.isPhasing())
                {
                    event.setY(event.getY() - 0.032);
                } /*else if (event.getStage() == Stage.PRE
                        && mc.world.getBlockState(PositionUtil.getPosition()).getBlock() != Blocks.AIR)
                {
                    mc.player.noClip = true;
                }*/
                if (event.getStage() == Stage.PRE
                        && module.isPhasing()
                        && mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() == Blocks.AIR)
                {
                    event.setY(event.getY() - 0.032);
                }
            case Normal:
                if (event.getStage() == Stage.PRE)
                {
                    if (mc.player.isSneaking()
                            && module.isPhasing()
                            && (!module.requireForward.getValue()
                            || mc.options.forwardKey.isPressed()))
                    {
                        if (checkAutoClick())
                        {
                            return;
                        }

                        float yaw = mc.player.yaw;
                        mc.player.setBoundingBox(
                                mc.player
                                        .getBoundingBox()
                                        .offset(
                                                module.distance.getValue()
                                                        * Math.cos(Math.toRadians(yaw + 90.0f)),
                                                0.0,
                                                module.distance.getValue()
                                                        * Math.sin(Math.toRadians(yaw + 90.0f))));
                    }
                }

                break;
            case Sand:
                mc.player.getVelocity().y = 0.0;
                if (mc.isWindowFocused())
                {
                    if (mc.player.input.jumping)
                    {
                        mc.player.getVelocity().y += 0.3;
                    }

                    if (mc.player.input.sneaking)
                    {
                        mc.player.getVelocity().y -= 0.3;
                    }
                }

                mc.player.noClip = true;
                break;
            case Packet:
                if (mc.player.horizontalCollision && module.timer.passed(200))
                {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(),
                            mc.player.getY() + 0.05,
                            mc.player.getZ(),
                            true));

                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX() + xSpeed * module.speed.getValue(),
                            mc.player.getY(),
                            mc.player.getZ() + zSpeed * module.speed.getValue(),
                            true));

                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(),
                            mc.player.getY(),
                            mc.player.getZ(),
                            true));

                    module.timer.reset();
                }
                break;
            case Skip:
                if (event.getStage() == Stage.PRE
                        && mc.player.horizontalCollision)
                {
                    if (!timer.passed(module.skipTime.getValue()))
                    {
                        if (module.cancel.getValue())
                        {
                            event.setCancelled(true);
                        }

                        return;
                    }

                    float direction = mc.player.yaw;
                    if (mc.player.forwardSpeed < 0F)
                    {
                        direction += 180F;
                    }

                    if (mc.player.sidewaysSpeed > 0F)
                    {
                        direction -= 90F * (mc.player.forwardSpeed < 0F
                                ? -0.5F
                                : mc.player.forwardSpeed > 0F ? 0.5F : 1F);
                    }

                    if (mc.player.sidewaysSpeed < 0F)
                    {
                        direction += 90F * (mc.player.forwardSpeed < 0F
                                ? -0.5F
                                : mc.player.forwardSpeed > 0F ? 0.5F : 1F);
                    }

                    double x = Math.cos(Math.toRadians(direction + 90)) * 0.2D;
                    double z = Math.sin(Math.toRadians(direction + 90)) * 0.2D;

                    if (module.limit.getValue())
                    {
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.onGround));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + x * 0.001f, mc.player.getY() + 0.1f, mc.player.getZ() + z * 0.001f, mc.player.onGround));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + x * 0.03f, 0, mc.player.getZ() + z * 0.03f, mc.player.onGround));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + x * 0.06f, mc.player.getY(), mc.player.getZ() + z * 0.06f, mc.player.onGround));
                        event.setCancelled(true);
                        timer.reset();
                        return;
                    }

                    for (int index = 0; index < off.length; index++)
                    {
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + off[index], mc.player.getZ(), mc.player.onGround));
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + (x * index), mc.player.getY(), mc.player.getZ() + (z * index), mc.player.onGround));
                    }

                    event.setCancelled(true);
                    timer.reset();
                }
                break;
            default:
        }
    }

    private boolean checkAutoClick()
    {
        if (!module.autoClick.getValue())
        {
            return false;
        }

        if (module.clickTimer.passed(module.clickDelay.getValue()))
        {
            HitResult result = mc.crosshairTarget;
            if (module.smartClick.getValue())
            {
                Direction facing = mc.player.getHorizontalFacing();
                BlockPos pos = PositionUtil.getPosition().offset(facing);
                if (!mc.player.getBoundingBox()
                        .intersects(new Box(pos)))
                {
                    pos = PositionUtil.getPosition();
                }

                if (mc.crosshairTarget != null
                        && pos.equals(mc.crosshairTarget.getPos())
                        || pos.up().equals(mc.crosshairTarget.getPos()))
                {
                    result = mc.crosshairTarget;
                }
                else
                {
                    BlockPos target = pos.up();
                    if (mc.world.getBlockState(target).isAir())
                    {
                        target = pos;
                    }

                    result = new BlockHitResult(
                            new Vec3d(0.0, 0.5, 0.0), // TODO: proper hitVec!
                            facing.getOpposite(),
                            target,
                            false);
                }
            }
            //noinspection ConstantConditions
            if (result != null && result.getPos() != null)
            {
                Hand hand =
                        mc.player.getOffHandStack()
                                .getItem().isFood()
                                || mc.player.getOffHandStack()
                                .getItem()
                                == Items.TOTEM_OF_UNDYING
                                ? Hand.OFF_HAND
                                : Hand.MAIN_HAND;

                Packet<?> packet = new PlayerInteractBlockC2SPacket(
                        hand,
                        new BlockHitResult(result.getPos(), Direction.UP, new BlockPos((int) result.getPos().getX(), (int) result.getPos().getY(), (int) result.getPos().getZ()), false),
                        0);

                NetworkUtil.sendPacketNoEvent(packet);
                module.pos = new BlockPos((int) result.getPos().getX(), (int) result.getPos().getY(), (int) result.getPos().getZ());
                module.clickTimer.reset();
            }
            else
            {
                return module.requireClick.getValue();
            }
        }
        else
        {
            return module.requireClick.getValue();
        }

        return false;
    }

}