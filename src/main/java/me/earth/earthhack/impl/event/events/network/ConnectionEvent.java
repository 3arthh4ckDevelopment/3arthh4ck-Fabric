package me.earth.earthhack.impl.event.events.network;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class ConnectionEvent
{
    private final PlayerEntity player;
    private final String name;
    private final UUID uuid;

    private ConnectionEvent(String name, UUID uuid, PlayerEntity player)
    {
        this.player = player;
        this.name   = name;
        this.uuid   = uuid;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    public String getName()
    {
        if (name == null && player != null)
        {
            return player.getName().getString();
        }

        return name;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public static class Join extends ConnectionEvent
    {
        public Join(String name, UUID uuid, PlayerEntity player)
        {
            super(name, uuid, player);
        }
    }

    public static class Leave extends ConnectionEvent
    {
        public Leave(String name, UUID uuid, PlayerEntity player)
        {
            super(name, uuid, player);
        }
    }

}
