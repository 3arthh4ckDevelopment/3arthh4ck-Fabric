package me.earth.earthhack.impl.modules.combat.selftrap;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3i;

public enum SelfTrapMode
{
    Obsidian(Blocks.OBSIDIAN, new Vec3i(0, 2, 0)),
    Web(Blocks.COBWEB, new Vec3i(0, 0, 0)),
    HighWeb(Blocks.COBWEB, new Vec3i(0, 1, 0)),
    FullWeb(Blocks.COBWEB, new Vec3i(0, 0, 0), new Vec3i(0, 1, 0));

    private final Vec3i[] offsets;
    private final Block block;

    SelfTrapMode(Block block, Vec3i...offsets)
    {
        this.offsets = offsets;
        this.block = block;
    }

    public Vec3i[] getOffsets()
    {
        return offsets;
    }

    public Block getBlock()
    {
        return block;
    }

}