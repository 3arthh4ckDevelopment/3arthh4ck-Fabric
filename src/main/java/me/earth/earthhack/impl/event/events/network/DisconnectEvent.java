package me.earth.earthhack.impl.event.events.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.MutableText;

/**
 * Note that this event gets posted asynchronously!
 */
public class DisconnectEvent
{
    private final MutableText component;
    private final ClientPlayNetworkHandler manager;

    public DisconnectEvent(MutableText component, ClientPlayNetworkHandler manager)
    {
        this.component = component;
        this.manager = manager;
    }

    public MutableText getComponent()
    {
        return component;
    }

    public ClientPlayNetworkHandler getManager()
    {
        return manager;
    }

}

