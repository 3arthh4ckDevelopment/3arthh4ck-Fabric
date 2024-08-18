package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

/**
 * Duck Interface for {@link PlayerInteractEntityC2SPacket}.
 */
public interface IPlayerInteractEntityC2S
{
    void setEntityId(int entityId);
    void setAction(PlayerInteractEntityC2SPacket.InteractTypeHandler action);
    int getEntityID();
    PlayerInteractEntityC2SPacket.InteractTypeHandler getAction();
    Entity getAttackedEntity();
}
