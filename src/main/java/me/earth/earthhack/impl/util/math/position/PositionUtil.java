package me.earth.earthhack.impl.util.math.position;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class PositionUtil implements Globals
{
    public static BlockPos getPosition()
    {
       return getPosition(mc.player); // for now
       // TODO: return getPosition(RotationUtil.getRotationPlayer());
    }

    public static BlockPos getPosition(Entity entity)
    {
        return getPosition(entity, 0.0);
    }

    public static BlockPos getPosition(Entity entity, double yOffset)
    {
        double y = entity.getY() + yOffset;
        if (entity.getY() - Math.floor(entity.getY()) > 0.5)
        {
            y = Math.ceil(entity.getY());
        }

        return new BlockPos((int) entity.getX(), (int) y, (int) entity.getZ());
    }

    public static Vec3d getEyePos()
    {
        return getEyePos(mc.player);
    }

    public static Vec3d getEyePos(Entity entity)
    {
        return new Vec3d(entity.getX(), getEyeHeight(entity), entity.getZ());
    }

    public static double getEyeHeight()
    {
        return getEyeHeight(mc.player);
    }

    public static double getEyeHeight(Entity entity)
    {
        return entity.getY() + entity.getEyeHeight(entity.getPose());
    }

    public static Set<BlockPos> getBlockedPositions(Entity entity)
    {
        return getBlockedPositions(entity.getBoundingBox());
    }

    public static Set<BlockPos> getBlockedPositions(Box bb)
    {
        return getBlockedPositions(bb, 0.5);
    }

    public static Set<BlockPos> getBlockedPositions(Box bb,
                                                    double offset)
    {
        Set<BlockPos> positions = new HashSet<>();

        double y = bb.minY;
        if (bb.minY - Math.floor(bb.minY) > offset)
        {
            y = Math.ceil(bb.minY);
        }

        positions.add(new BlockPos((int) bb.maxX, (int) y, (int) bb.maxZ));
        positions.add(new BlockPos((int) bb.minX, (int) y, (int) bb.minZ));
        positions.add(new BlockPos((int) bb.maxX, (int) y, (int) bb.minZ));
        positions.add(new BlockPos((int) bb.minX, (int) y, (int) bb.maxZ));

        return positions;
    }

    public static boolean isBoxColliding()
    {
        return mc.world.getEntityCollisions(mc.player,
                mc.player.getBoundingBox()
                        .offset(0.0, 0.21, 0.0)).size() > 0;
    }

    public static Entity getPositionEntity()
    {
        PlayerEntity player = mc.player;
        Entity ridingEntity;
        return player == null
                ? null
                : (ridingEntity = player.getVehicle()) != null
                && !(ridingEntity instanceof BoatEntity)
                ? ridingEntity
                : player;
    }

    public static Entity requirePositionEntity()
    {
        return Objects.requireNonNull(getPositionEntity());
    }

    public static boolean inLiquid()
    {
        return inLiquid(MathHelper.floor(
                requirePositionEntity().getBoundingBox().minY + 0.01));
    }

    public static boolean inLiquid(boolean feet)
    {
        return inLiquid(MathHelper.floor(
                requirePositionEntity().getBoundingBox().minY
                        - (feet ? 0.03 : 0.2)));
    }

    private static boolean inLiquid(int y)
    {
        return findState(FluidBlock.class, y) != null;
    }

    private static BlockState findState(Class<? extends Block> block, int y)
    {
        Entity entity = requirePositionEntity();
        int startX = MathHelper.floor(entity.getBoundingBox().minX);
        int startZ = MathHelper.floor(entity.getBoundingBox().minZ);
        int endX   = MathHelper.ceil(entity.getBoundingBox().maxX);
        int endZ   = MathHelper.ceil(entity.getBoundingBox().maxZ);
        for (int x = startX; x < endX; x++)
        {
            for (int z = startZ; z < endZ; z++)
            {
                BlockState s = mc.world.getBlockState(new BlockPos(x, y, z));
                if (block.isInstance(s.getBlock()))
                {
                    return s;
                }
            }
        }

        return null;
    }

    public static boolean isMovementBlocked()
    {
        BlockState state = findState(Block.class,
                MathHelper.floor(mc.player.getBoundingBox().minY - 0.01));
        return state != null /* && state.getBlock() */;
    }

    public static boolean isAbove(BlockPos pos)
    {
        return mc.player.getBoundingBox().minY >= pos.getY();
    }

    public static BlockPos fromBB(Box bb)
    {
        return new BlockPos((int) (bb.minX + bb.maxX) / 2,
                (int)(bb.minY + bb.maxY) / 2,
                (int) (bb.minZ + bb.maxZ) / 2);
    }

    public static boolean intersects(Box bb, BlockPos pos)
    {
        return bb.intersects(pos.getX(),
                pos.getY(),
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 1,
                pos.getZ() + 1);
    }

}
