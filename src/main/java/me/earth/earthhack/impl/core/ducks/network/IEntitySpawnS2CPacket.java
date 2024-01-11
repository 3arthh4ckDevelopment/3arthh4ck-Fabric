package me.earth.earthhack.impl.core.ducks.network;

public interface IEntitySpawnS2CPacket
{
    void setAttacked(boolean attacked);

    boolean isAttacked();
}
