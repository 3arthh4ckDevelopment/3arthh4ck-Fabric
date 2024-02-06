package me.earth.earthhack.impl.modules.player.automine.util;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.DistanceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

// TODO: more generic approach maybe map BlockPositions to BlockStates
public class Constellation implements IConstellation
{
    protected static final ModuleCache<Speedmine> SPEEDMINE =
        Caches.getModule(Speedmine.class);

    protected final PlayerEntity player;
    protected final BlockPos playerPos;
    protected final BlockState state;
    protected final BlockState playerState;
    protected final BlockPos pos;
    protected final AutoMine autoMine;
    protected boolean selfUntrap;
    protected boolean burrow;
    protected boolean l;

    public Constellation(WorldAccess world,
                         PlayerEntity player,
                         BlockPos pos,
                         BlockPos playerPos,
                         BlockState state,
                         AutoMine autoMine)
    {
        this.player      = player;
        this.pos         = pos;
        this.playerPos   = playerPos;
        this.autoMine    = autoMine;
        this.playerState = world.getBlockState(playerPos);
        this.state       = state;
    }

    @Override
    public boolean isAffected(BlockPos pos, BlockState state)
    {
        return this.pos.equals(pos)
            && this.state.getBlock() != state.getBlock();
    }

    @Override
    public boolean isValid(WorldAccess world, boolean checkPlayerState)
    {
        BlockState s;
        boolean speedmineCheck =
            (autoMine.dependOnSMCheck.getValue()
                    || autoMine.speedmineCrystalDamageCheck.getValue())
                && !isBurrow()
                && !isSelfUntrap()
                && !isL()
                && SPEEDMINE.returnIfPresent(sm -> sm.crystalHelper
                            .calcCrystal(pos, player, true), null) != null;
        // Can't test this with a FakePlayer!
        return (PositionUtil.getPosition(player).equals(playerPos)
            || (isBurrow()
                && autoMine.extraBurrowCheck.getValue()
                && DistanceUtil.distanceSq2Bottom(playerPos, player) <= 1.5)
            || (isSelfUntrap()
                && autoMine.untrapCheck.getValue()
                && DistanceUtil.distanceSq2Bottom(playerPos) <= 1.5)
            || speedmineCheck)
            && (!autoMine.dependOnSMCheck.getValue()
                || isL()
                || isBurrow()
                || isSelfUntrap()
                || speedmineCheck)
            && ((s = world.getBlockState(pos)).getBlock() == state.getBlock()
                || autoMine.multiBreakCheck.getValue()
                    && SPEEDMINE.returnIfPresent(Speedmine::getMode,
                                                 MineMode.Smart)
                                .isMultiBreaking
                    && s.getBlock() == Blocks.AIR)
            && (!checkPlayerState
                || world.getBlockState(playerPos).getBlock()
                        == playerState.getBlock());
    }

    public boolean isBurrow()
    {
        return burrow;
    }

    public void setBurrow(boolean burrow)
    {
        this.burrow = burrow;
    }

    public boolean isL()
    {
        return l;
    }

    public void setL(boolean l)
    {
        this.l = l;
    }

    public boolean isSelfUntrap()
    {
        return selfUntrap;
    }

    public void setSelfUntrap(boolean selfUntrap)
    {
        this.selfUntrap = selfUntrap;
    }

}
