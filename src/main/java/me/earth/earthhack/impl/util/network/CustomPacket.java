package me.earth.earthhack.impl.util.network;

import net.minecraft.network.NetworkPhase;

public interface CustomPacket
{
    int getId() throws Exception;

    default NetworkPhase getState()
    {
        return NetworkPhase.PLAY;
    }

}
