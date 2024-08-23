package me.earth.earthhack.impl.util.math.raytrace;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/**
 * Performs
 * {@link BlockView#raycastBlock(Vec3d, Vec3d, BlockPos, VoxelShape, BlockState)}.
 */
@FunctionalInterface
public interface CollisionFunction
{
    BlockView blockView = MinecraftClient.getInstance().world;

    /**
     * {@link BlockView#raycastBlock(Vec3d, Vec3d, BlockPos, VoxelShape, BlockState)}
     */
    CollisionFunction DEFAULT = (start, end, pos, shape, state) -> blockView.raycastBlock(start, end, pos, shape, state);

    BlockHitResult collisionRayTrace(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state);

}
