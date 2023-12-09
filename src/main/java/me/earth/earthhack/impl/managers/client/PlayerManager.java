package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.observable.Observable;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.event.PlayerEvent;
import me.earth.earthhack.impl.managers.client.event.PlayerEventType;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends Observable<PlayerEvent>
{
    private final Map<String, UUID> players = new ConcurrentHashMap<>();

    public boolean contains(Entity player)
    {
        if (player instanceof PlayerEntity)
        {
            return contains(player.getName().toString());
        }

        return false;
    }

    /**
     * Still here cause I'm too lazy to update plugins...
     */
    public boolean contains(PlayerEntity player)
    {
        return contains(player.getName().toString());
    }

    public boolean contains(String name)
    {
        return players.containsKey(name);
    }

    public void add(PlayerEntity player)
    {
        add(player.getName().toString(), player.getUuid());
    }

    public void add(String name)
    {
        Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.UUID, name)
        {
            @Override
            public void onSuccess()
            {
                add(name, uuid);
            }

            @Override
            public void onFailure()
            {
                /* Nothing */
            }
        });
    }

    public void add(String name, UUID uuid)
    {
        PlayerEvent event = new PlayerEvent(PlayerEventType.ADD, name, uuid);
        onChange(event);
        if (!event.isCancelled())
        {
            players.put(name, uuid);
        }
    }

    public void remove(Entity player)
    {
        if (player instanceof PlayerEntity)
        {
            remove(player.getName().toString());
        }
    }

    public void remove(String name)
    {
        if (players.containsKey(name))
        {
            UUID uuid = players.get(name);
            PlayerEvent event = new PlayerEvent(PlayerEventType.DEL, name, uuid);
            onChange(event);

            if (!event.isCancelled())
            {
                players.remove(name);
            }
        }
    }

    public Collection<String> getPlayers()
    {
        return players.keySet();
    }

    public Map<String, UUID> getPlayersWithUUID()
    {
        return players;
    }

    public void clear()
    {
        players.clear();
    }

}
