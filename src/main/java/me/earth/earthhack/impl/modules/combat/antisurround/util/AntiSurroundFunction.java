package me.earth.earthhack.impl.modules.combat.antisurround.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@FunctionalInterface
public interface AntiSurroundFunction
{
    void accept(BlockPos pos,
                BlockPos down,
                BlockPos on,
                Direction onFacing,
                int obbySlot,
                MineSlots slots,
                int crystalSlot,
                Entity blocking,
                PlayerEntity found,
                boolean execute);
}