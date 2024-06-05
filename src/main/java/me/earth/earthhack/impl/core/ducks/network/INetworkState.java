package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.network.packet.Packet;

import java.util.Set;

public interface INetworkState {
    Set<Class<? extends Packet<?>>> earthhack$getPackets();
}
