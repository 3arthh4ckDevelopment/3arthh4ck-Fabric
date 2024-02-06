package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Performs
 * {@link IBlockProperties#collisionRayTrace(World, BlockPos, Vec3d, Vec3d)}.
 */
@FunctionalInterface
public interface CollisionFunction
{
    /**
     * {@link IBlockProperties#collisionRayTrace(World, BlockPos, Vec3d, Vec3d)}
     */
    // CollisionFunction DEFAULT = IBlockProperties::collisionRayTrace; //TODO: fix
    CollisionFunction DEFAULT = null;

    BlockHitResult collisionRayTrace(BlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end);
}
