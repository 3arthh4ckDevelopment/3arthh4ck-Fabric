package me.earth.earthhack.impl.core.ducks.network;

public interface IPlayerActionC2SPacket
{
    void earthhack$setClientSideBreaking(boolean breaking);

    boolean earthhack$isClientSideBreaking();

    void earthhack$setNormalDigging(boolean normalDigging);

    /**
     * Signalizes that this packet is coming from Minecraft's code rather
     * than from the client and should not get send though the PingBypass
     * when Speedmine is active.
     */
    boolean earthhack$isNormalDigging();

}
