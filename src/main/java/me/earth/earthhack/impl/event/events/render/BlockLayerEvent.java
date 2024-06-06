package me.earth.earthhack.impl.event.events.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;

public class BlockLayerEvent
{
    private BlockRenderType layer = null;
    private final Block block;

    public BlockLayerEvent(Block block)
    {
        this.block = block;
    }

    public void setLayer(BlockRenderType layer)
    {
        this.layer = layer;
    }

    public BlockRenderType getLayer()
    {
        return this.layer;
    }

    public Block getBlock()
    {
        return block;
    }

}
