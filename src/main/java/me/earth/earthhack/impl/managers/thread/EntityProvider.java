package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Makes snapshots of {@link ClientWorld#getEntities()}, {@link ClientWorld#getPlayers()} and {@link WorldChunk#getBlockEntities()}
 * so you can access them on another thread.
 */
@SuppressWarnings("unused")
public class EntityProvider extends SubscriberImpl implements Globals
{
    private volatile List<PlayerEntity> players;
    private volatile List<Entity> entities;
    private volatile List<BlockEntity> blockEntities;

    public EntityProvider()
    {
        this.players  = Collections.emptyList();
        this.entities = Collections.emptyList();
        this.blockEntities = Collections.emptyList();

        this.listeners.add(new EventListener<>(TickEvent.class)
        {
            @Override
            public void invoke(TickEvent event)
            {
                update();
            }
        });

        this.listeners.add(new EventListener<>(
                TickEvent.PostWorldTick.class) {
            @Override
            public void invoke(TickEvent.PostWorldTick event) {
                update();
            }
        });
    }

    private void update()
    {
        if (mc.world != null)
        {
            setLists(
                new ArrayList<>(getEntities(false)),
                new ArrayList<>(getPlayers(false)),
                new ArrayList<>(getBlockEntities(false)));
        }
        else
        {
            setLists(Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities,
                          List<PlayerEntity> playerEntities,
                          List<BlockEntity> loadedBlockEntities)
    {
        entities = loadedEntities;
        players  = playerEntities;
        blockEntities = loadedBlockEntities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     *  @return copy of {@link ClientWorld#getEntities()}
     */
    public List<Entity> getEntities()
    {
        return entities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     * @return copy of {@link ClientWorld#getPlayers()}
     */
    public List<PlayerEntity> getPlayers()
    {
        return players;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     * @return copy of {@link WorldChunk#getBlockEntities()}
     */
    public List<BlockEntity> getBlockEntities()
    {
        return blockEntities;
    }

    public List<Entity> getEntitiesAsync()
    {
        return getEntities(!mc.isOnThread());
    }

    public List<PlayerEntity> getPlayersAsync()
    {
        return getPlayers(!mc.isOnThread());
    }

    public List<BlockEntity> getBlockEntitiesAsync()
    {
        return getBlockEntities(!mc.isOnThread());
    }

    public List<Entity> getEntities(boolean async)
    {
        if (async)
            return entities;
        List<Entity> entityList = new ArrayList<>();
        mc.world.getEntities().forEach(entityList::add);
        return entityList;
    }

    public List<PlayerEntity> getPlayers(boolean async)
    {
        return async ? players : new ArrayList<>(mc.world.getPlayers());
    }

    public List<BlockEntity> getBlockEntities(boolean async) {
        if (async)
            return blockEntities;

        List<BlockEntity> list = new ArrayList<>();
        int chunkDistance = Math.min(mc.options.getViewDistance().getValue(), 4);

        for (int x = -chunkDistance; x <= chunkDistance; x++) {
            for (int z = -chunkDistance; z <= chunkDistance; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);

                if (chunk != null) {
                    list.addAll(chunk.getBlockEntities().values());
                }
            }
        }
        return list;
    }

    public Entity getEntity(int id)
    {
        List<Entity> entities = getEntitiesAsync();
        if (entities != null)
        {
            return entities.stream()
                           .filter(e -> e != null && e.getId() == id)
                           .findFirst()
                           .orElse(null);
        }

        return null;
    }

}
