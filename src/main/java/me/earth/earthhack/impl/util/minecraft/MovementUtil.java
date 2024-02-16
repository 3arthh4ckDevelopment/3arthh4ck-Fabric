package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class MovementUtil implements Globals
{
    public static boolean isMoving()
    {
        return mc.player.input.movementForward != 0.0 || mc.player.input.movementSideways != 0.0;
    }

    public static boolean anyMovementKeys()
    {
        return mc.player.input.pressingForward
                || mc.player.input.pressingBack
                || mc.player.input.pressingLeft
                || mc.player.input.pressingRight
                || mc.player.input.jumping
                || mc.player.input.sneaking;
    }

    public static boolean noMovementKeys()
    {
        return !mc.player.input.pressingForward
                && !mc.player.input.pressingBack
                && !mc.player.input.pressingRight
                && !mc.player.input.pressingLeft;
    }

    public static boolean noMovementKeysOrJump()
    {
        return noMovementKeys()
                && !Keyboard.isKeyDown(mc.options.jumpKey.getDefaultKey().getCode());
    }

    public static void setMoveSpeed(double speed) {
        double forward = mc.player.input.movementForward;
        double strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getHeadYaw();
        if (forward == 0.0 && strafe == 0.0) {
            mc.player.setVelocity(0.0, mc.player.getVelocity().getY(), 0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            mc.player.setVelocity(
                    forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)),
                    mc.player.getVelocity().getY(),
                    forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }


    public static void strafe(MoveEvent event, double speed)
    {
        if (isMoving())
        {
            double[] strafe = strafe(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        }
        else
        {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static double[] strafe(double speed)
    {
        return strafe(mc.player, speed);
    }

    public static double[] strafe(Entity entity, double speed)
    {
        return strafe(entity, mc.player.input, speed);
    }

    public static double[] strafe(Entity entity,
                                  Input movementInput,
                                  double speed)
    {
        float moveForward = movementInput.movementForward;
        float moveStrafe  = movementInput.movementSideways;
        float rotationYaw = entity.prevYaw
                + (entity.getYaw() - entity.prevYaw)
                * mc.getTickDelta();

        if (moveForward != 0.0f)
        {
            if (moveStrafe > 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f)
            {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f)
            {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f)
            {
                moveForward = -1.0f;
            }
        }

        double posX =
                moveForward * speed * -Math.sin(Math.toRadians(rotationYaw))
                        + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ =
                moveForward * speed * Math.cos(Math.toRadians(rotationYaw))
                        - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));

        return new double[] {posX, posZ};
    }

    public static Input inverse(Entity entity, double speed)
    {
        Input input = new Input();
        input.sneaking = entity.isSneaking();

        for (float d = -1.0f; d <= 1.0f; d += 1.0f)
        {
            for (float e = -1.0f; e <= 1.0f; e += 1.0f)
            {
                Input dummyInput = new Input();
                dummyInput.movementForward = d;
                dummyInput.movementSideways = e;
                dummyInput.sneaking = entity.isSneaking();
                double[] moveVec = strafe(entity, dummyInput, speed);
                if (entity.isSneaking())
                {
                    moveVec[0] *= 0.3f;
                    moveVec[1] *= 0.3f;
                }

                double targetMotionX = moveVec[0];
                double targetMotionZ = moveVec[1];
                if ((targetMotionX < 0 ? entity.getVelocity().getX() <= targetMotionX : entity.getVelocity().getX() >= targetMotionX)
                        && (targetMotionZ < 0 ? entity.getVelocity().getZ() <= targetMotionZ : entity.getVelocity().getZ() >= targetMotionZ))
                {
                    input.movementForward = d;
                    input.movementSideways = e;
                    break;
                }
            }
        }

        return input;
    }

    public static double getDistance2D()
    {
        double xDist = mc.player.getPos().x - mc.player.prevX;
        double zDist = mc.player.getPos().z - mc.player.prevZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double getDistance3D()
    {
        double xDist = mc.player.getPos().x  - mc.player.prevX;
        double yDist = mc.player.getPos().y  - mc.player.prevY;
        double zDist = mc.player.getPos().z  - mc.player.prevZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    // TODO: Slowness?
    public static double getSpeed()
    {
        return getSpeed(false);
    }

    public static double getSpeed(boolean slowness, double defaultSpeed)
    {

        if (mc.player.hasStatusEffect(StatusEffects.SPEED))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SPEED))
                    .getAmplifier();

            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SLOWNESS))
                    .getAmplifier();

            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getSpeed(boolean slowness)
    {
        double defaultSpeed = 0.2873;

        if (mc.player.hasStatusEffect(StatusEffects.SPEED))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SPEED))
                    .getAmplifier();

            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slowness && mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SLOWNESS))
                    .getAmplifier();

            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }

        return defaultSpeed;
    }

    public static double getJumpSpeed()
    {
        double defaultSpeed = 0.0;

        if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
        {
            //noinspection ConstantConditions
            int amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
            defaultSpeed += (amplifier + 1) * 0.1;
        }

        return defaultSpeed;
    }

    public static boolean isInMovementDirection(double x, double y, double z)
    {
        if (mc.player.getVelocity().getX() != 0.0 || mc.player.getVelocity().getZ() != 0.0)
        {
            BlockPos movingPos = new BlockPos(mc.player.getBlockPos())
                    .add((int) mc.player.getVelocity().getX() * 10000, 0, (int) mc.player.getVelocity().getZ() * 10000);

            BlockPos antiPos   = new BlockPos(mc.player.getBlockPos())
                    .add((int) mc.player.getVelocity().getX() * -10000, 0, (int) mc.player.getVelocity().getY() * -10000);
            return movingPos.getSquaredDistance(x, y, z) < antiPos.getSquaredDistance(x, y, z);
        }

        return true;
    }

}
