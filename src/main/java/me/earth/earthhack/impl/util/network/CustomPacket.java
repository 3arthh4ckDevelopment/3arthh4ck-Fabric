package me.earth.earthhack.impl.util.network;

import net.minecraft.network.NetworkState;

public interface CustomPacket
{
    int getId() throws Exception;

    default NetworkState getState()
    {
        return NetworkState.PLAY;
    }

}
