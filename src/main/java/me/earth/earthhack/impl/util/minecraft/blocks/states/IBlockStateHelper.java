package me.earth.earthhack.impl.util.minecraft.blocks.states;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public interface IBlockStateHelper extends IBlockAccess
{
    default void addAir(BlockPos pos)
    {
        this.addBlockState(pos, Blocks.AIR.getDefaultState());
    }

    void addBlockState(BlockPos pos, BlockState state);

    void delete(BlockPos pos);

    void clearAllStates();

}
