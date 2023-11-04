package me.earth.earthhack.impl.event.events.network;

import net.minecraft.client.world.ClientWorld;

public class WorldClientEvent
{
    private final ClientWorld client;

    private WorldClientEvent(ClientWorld client)
    {
        this.client = client;
    }

    public ClientWorld getClient()
    {
        return client;
    }

    public static class Load extends WorldClientEvent
    {
        public Load(ClientWorld client)
        {
            super(client);
        }
    }

    public static class Unload extends WorldClientEvent
    {
        public Unload(ClientWorld client)
        {
            super(client);
        }
    }

}
