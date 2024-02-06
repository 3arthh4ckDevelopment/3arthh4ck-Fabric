package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class MoveEvent extends Event
{
    private final MovementType type;
    private double x;
    private double y;
    private double z;
    private Vec3d vec;
    private boolean sneaking;

    public MoveEvent(MovementType type, double x, double y, double z, boolean sneaking)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sneaking = sneaking;
    }

    public MoveEvent(MovementType type, Vec3d vec, boolean sneaking)
    {
        this.type = type;
        this.vec = vec;
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.sneaking = sneaking;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }
    public void setVec(Vec3d vec){
        this.vec = vec;
    }
    public Vec3d getVec(){
        return vec;
    }
    public MovementType getType()
    {
        return type;
    }

    public boolean isSneaking()
    {
        return sneaking;
    }

    public void setSneaking(boolean sneaking)
    {
        this.sneaking = sneaking;
    }

}
