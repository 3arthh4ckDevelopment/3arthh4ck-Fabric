package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

// TODO: remove this, use RayCastContext instead (THIS IS SHIT)
public class RayTraceResult
{
    /** Used to determine what sub-segment is hit */
    public int subHit = -1;

    /** Used to add extra hit info */
    public Object hitInfo = null;

    private BlockPos blockPos;
    /** The type of hit that occured, see {@link Type} for possibilities. */
    public RayTraceResult.Type typeOfHit;
    public Direction sideHit;
    /** The vector position of the hit */
    public Vec3d hitVec;
    /** The hit entity */
    public Entity entityHit;

    public RayTraceResult(Vec3d hitVecIn, Direction sideHitIn, BlockPos blockPosIn)
    {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
    }

    public RayTraceResult(Vec3d hitVecIn, Direction sideHitIn)
    {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
    }

    public RayTraceResult(Entity entityIn)
    {
        this(entityIn, new Vec3d(entityIn.getX(), entityIn.getY(), entityIn.getZ()));
    }

    public RayTraceResult(RayTraceResult.Type typeIn, Vec3d hitVecIn, Direction sideHitIn, BlockPos blockPosIn)
    {
        this.typeOfHit = typeIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new Vec3d(hitVecIn.x, hitVecIn.y, hitVecIn.z);
    }

    public RayTraceResult(Entity entityHitIn, Vec3d hitVecIn)
    {
        this.typeOfHit = RayTraceResult.Type.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos()
    {
        return this.blockPos;
    }

    public String toString()
    {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public enum Type
    {
        MISS,
        BLOCK,
        ENTITY;
    }
}