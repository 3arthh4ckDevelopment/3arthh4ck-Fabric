package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import java.util.HashSet;
import java.util.Set;


public class ShulkerConstellation implements IConstellation
{
    private final BlockPos pos;
    protected Set<Block> getBlocks()
    {
        return new HashSet<>(SpecialBlocks.SHULKERS);
    }

    public ShulkerConstellation(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public boolean isAffected(BlockPos pos, BlockState state)
    {
        return this.pos.equals(pos) && state.getBlock() != getBlocks();
    }

    @Override
    public boolean isValid(WorldAccess world, boolean checkPlayerState)
    {
        return world.getBlockState(pos).getBlock() == getBlocks();
    }

    @Override
    public boolean cantBeImproved()
    {
        return false;
    }

}