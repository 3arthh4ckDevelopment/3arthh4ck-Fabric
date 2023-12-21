package me.earth.earthhack.impl.managers.thread;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Makes snapshots of {@link ClientWorld#getEntities()} and
 * {@link ClientWorld#getPlayers()} so you can access them
 * on another thread.
 */
@SuppressWarnings("unused")
public class EntityProvider extends SubscriberImpl implements Globals
{
    private volatile List<PlayerEntity> players;
    private volatile List<Entity> entities;

    public EntityProvider()
    {
        this.players  = Collections.emptyList();
        this.entities = Collections.emptyList();
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
            List<Entity> entityList = new ArrayList<>();
            mc.world.getEntities().forEach(entityList::add);
            setLists(
                new ArrayList<>(entityList),
                new ArrayList<>(mc.world.getPlayers()));
        }
        else
        {
            setLists(Collections.emptyList(),
                    Collections.emptyList());
        }
    }

    private void setLists(List<Entity> loadedEntities,
                          List<PlayerEntity> playerEntities)
    {
        entities = loadedEntities;
        players  = playerEntities;
    }

    /**
     *  Always store this in a local variable when
     *  you are calling this from another thread!
     *  Might be null and might contain nullpointers.
     *
     *  @return copy of {@link net.minecraft.client.world.ClientWorld#getEntities()}
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
     * @return copy of {@link net.minecraft.client.world.ClientWorld#getPlayers()}
     */
    public List<PlayerEntity> getPlayers()
    {
        return players;
    }

    public List<Entity> getEntitiesAsync()
    {
        return getEntities(!mc.isOnThread());
    }

    public List<PlayerEntity> getPlayersAsync()
    {
        return getPlayers(!mc.isOnThread());
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

    public static List<Entity> filterEntityByClass(Class<? extends Entity> classEntity, List<Entity> entityList) { //TODO: move this to a util (?)
        List<Entity> filteredEntities = new ArrayList<>();
        for (Entity entity : entityList)
            if (entity.getClass() == classEntity)
                filteredEntities.add(entity);
        return filteredEntities;
    }

}
