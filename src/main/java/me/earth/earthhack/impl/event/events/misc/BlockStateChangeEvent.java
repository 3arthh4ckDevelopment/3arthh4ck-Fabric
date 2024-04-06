package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.impl.core.ducks.world.IChunk;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public record BlockStateChangeEvent(BlockPos pos, BlockState state, IChunk chunk) { }
