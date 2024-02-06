package me.earth.earthhack.impl.modules.player.automine.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class EchestConstellation implements IConstellation
{
    private final BlockPos pos;

    public EchestConstellation(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public boolean isAffected(BlockPos pos, BlockState state)
    {
        return this.pos.equals(pos) && state.getBlock() != Blocks.ENDER_CHEST;
    }

    @Override
    public boolean isValid(WorldAccess world, boolean checkPlayerState)
    {
        return world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    @Override
    public boolean cantBeImproved()
    {
        return false;
    }

}
