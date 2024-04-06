package me.earth.earthhack.impl.event.events.render;

import net.minecraft.world.chunk.Chunk;

public record UnloadChunkEvent(Chunk chunk) {

}
