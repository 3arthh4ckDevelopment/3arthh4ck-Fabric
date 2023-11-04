package me.earth.earthhack.impl.event.events.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.MutableText;

public class IntegratedDisconnectEvent extends DisconnectEvent {
    public IntegratedDisconnectEvent(MutableText component, ClientPlayNetworkHandler manager) {
        super(component, manager);
    }

}