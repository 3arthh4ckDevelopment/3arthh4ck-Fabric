package me.earth.earthhack.impl.event.events.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.MutableText;

/**
 * Note that this event gets posted asynchronously!
 */
public class DisconnectEvent
{
    private final MutableText component;
    private final ClientConnection manager;

    public DisconnectEvent(MutableText component, ClientConnection manager)
    {
        this.component = component;
        this.manager = manager;
    }

    public MutableText getComponent()
    {
        return component;
    }

    public ClientConnection getManager()
    {
        return manager;
    }

}

