package me.earth.earthhack.impl.util.minecraft.blocks.states;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public interface IBlockAccess
{
    @Nullable
    BlockEntity getTileEntity(BlockPos pos);

    int getCombinedLight(BlockPos pos, int lightValue);

    BlockState getBlockState(BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(BlockPos pos);

    Biome getBiome(BlockPos pos);

    // int getStrongPower(BlockPos pos, Direction direction);

    WorldCreator.WorldType getWorldType();

    /**
     * FORGE: isSideSolid, pulled up from {@link World}
     *
     * @param pos Position
     * @param side Side
     * @param _default default return value
     * @return if the block is solid on the side
     */
    boolean isSideSolid(BlockPos pos, Direction side, boolean _default);
}