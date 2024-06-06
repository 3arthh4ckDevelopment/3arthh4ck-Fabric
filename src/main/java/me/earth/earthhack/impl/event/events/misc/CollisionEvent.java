package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/**
 * Note that {@link net.minecraft.client.MinecraftClient#player} could
 * be null when this event is fired.
 *
 * This Event will no longer be fired on the Bus because it
 * was called quite often (Particles?).
 * TODO: Profile again, with the new ParticleThing this should be fine!
 */
public class CollisionEvent extends Event
{
    private final Entity entity;
    private final BlockPos pos;
    private final Block block;

    private Box bb;

    public CollisionEvent(BlockPos pos,
                          Box bb,
                          Entity entity,
                          Block block)
    {
        this.pos = pos;
        this.bb = bb;
        this.entity = entity;
        this.block = block;
    }

    public Box getBB()
    {
        return bb;
    }

    public void setBB(Box bb)
    {
        this.bb = bb;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public Block getBlock()
    {
        return block;
    }

    public interface Listener
    {
        void onCollision(CollisionEvent event);
    }

}