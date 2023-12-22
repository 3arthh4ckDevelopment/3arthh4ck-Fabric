package me.earth.earthhack.impl.event.events.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.text.MutableText;

public class IntegratedDisconnectEvent extends DisconnectEvent {
    public IntegratedDisconnectEvent(MutableText component, ClientConnection manager) {
        super(component, manager);
    }

}