package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Position
{
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final float head;
    private final double motionX;
    private final double motionY;
    private final double motionZ;
    private final Vec3d velocity;

    public Position(PlayerEntity player)
    {
        this.x       = player.getX();
        this.y       = player.getY();
        this.z       = player.getZ();
        this.yaw     = player.getYaw();
        this.pitch   = player.getPitch();
        this.head    = player.getHeadYaw();
        this.velocity = new Vec3d(
                player.getVelocity().getX(),
                player.getVelocity().getY(),
                player.getVelocity().getZ());
        this.motionX = player.getVelocity().getX();
        this.motionY = player.getVelocity().getY();
        this.motionZ = player.getVelocity().getZ();
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getHead()
    {
        return head;
    }

    public double getMotionX()
    {
        return motionX;
    }

    public double getMotionY()
    {
        return motionY;
    }

    public double getMotionZ()
    {
        return motionZ;
    }
    public Vec3d getVelocity() {
        return velocity;
    }
}