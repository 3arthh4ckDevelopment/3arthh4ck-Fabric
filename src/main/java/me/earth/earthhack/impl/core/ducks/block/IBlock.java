package me.earth.earthhack.impl.core.ducks.block;

import net.minecraft.block.BlockState;

public interface IBlock
{
    String getHarvestToolNonForge(BlockState state);

    int getHarvestLevelNonForge(BlockState state);

}
