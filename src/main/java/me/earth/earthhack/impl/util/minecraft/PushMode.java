package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Random;

public enum PushMode implements Globals {
    None() {
        @Override
        public void pushOutOfBlocks(
            Entity entity, double x, double y, double z) {
            // NOP
        }
    },
    MP() {
        private final Random rand = new Random();

        @Override
        public void pushOutOfBlocks(
            Entity entity, double x, double y, double z) {
            BlockPos blockpos = new BlockPos((int) x, (int) y, (int) z);
            double d0 = x - blockpos.getX();
            double d1 = y - blockpos.getY();
            double d2 = z - blockpos.getZ();
            if (mc.world.canCollide(entity, entity.getBoundingBox()))
            {
                Direction enumfacing = Direction.UP;
                double d3 = Double.MAX_VALUE;

                if (!mc.world.getBlockState(blockpos.west()).isFullCube(entity.getWorld(), blockpos.west()) && d0 < d3)
                {
                    d3 = d0;
                    enumfacing = Direction.WEST;
                }

                if (!mc.world.getBlockState(blockpos.east()).isFullCube(entity.getWorld(), blockpos.east()) && 1.0D - d0 < d3)
                {
                    d3 = 1.0D - d0;
                    enumfacing = Direction.EAST;
                }

                if (!mc.world.getBlockState(blockpos.north()).isFullCube(entity.getWorld(), blockpos.north()) && d2 < d3)
                {
                    d3 = d2;
                    enumfacing = Direction.NORTH;
                }

                if (!mc.world.getBlockState(blockpos.south()).isFullCube(entity.getWorld(), blockpos.south()) && 1.0D - d2 < d3)
                {
                    d3 = 1.0D - d2;
                    enumfacing = Direction.SOUTH;
                }

                if (!mc.world.getBlockState(blockpos.up()).isFullCube(entity.getWorld(), blockpos.up()) && 1.0D - d1 < d3)
                {
                    d3 = 1.0D - d1;
                    enumfacing = Direction.UP;
                }

                float f = rand.nextFloat() * 0.2F + 0.1F;
                float f1 = (float) enumfacing.getDirection().offset();
                entity.getVelocity().multiply(0);

                if (enumfacing.getAxis() == Direction.Axis.X) {
                    entity.getVelocity().add(f1 * f, entity.getVelocity().getY() * 0.75D, entity.getVelocity().getZ() * 0.75D);
                }
                else if (enumfacing.getAxis() == Direction.Axis.Y) {
                    entity.getVelocity().add(entity.getVelocity().getX() * 0.75D, f1 * f, entity.getVelocity().getZ() * 0.75D);
                }
                else if (enumfacing.getAxis() == Direction.Axis.Z) {
                    entity.getVelocity().add(entity.getVelocity().getX() * 0.75D, entity.getVelocity().getY() * 0.75D, f1 * f);
                }
            }
        }
    };

    public abstract void pushOutOfBlocks(
        Entity entity, double x, double y, double z);

    private static boolean isHeadspaceFree(BlockPos pos, int height)
    {
        for (int y = 0; y < height; y++)
        {
            if (!isOpenBlockSpace(pos.add(0, y, 0))) return false;
        }

        return true;
    }

    private static boolean isOpenBlockSpace(BlockPos pos)
    {
        BlockState iblockstate = mc.world.getBlockState(pos);
        return !iblockstate.isFullCube(mc.world, pos);
    }

}
