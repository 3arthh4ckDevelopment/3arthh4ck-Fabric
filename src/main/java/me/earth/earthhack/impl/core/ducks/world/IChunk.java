package me.earth.earthhack.impl.core.ducks.world;

public interface IChunk
{
    boolean earthhack$isCompilingHoles();

    void earthhack$setCompilingHoles(boolean compilingHoles);

    void earthhack$addHoleTask(Runnable task);

    /**
     * @return the current version of this chunk. Whenever this chunk is loaded or unloaded this number will be
     * incremented by one. That way every Hole found in a previous version of this Chunk becomes invalid.
     */
    int earthhack$getHoleVersion();

    void earthhack$setHoleVersion(int version);
}
