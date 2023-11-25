package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Duck Interface for {@link net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket}.
 */
public interface IPlayerInteractEntityC2S
{
    void setEntityId(int entityId);

    void setAction(PlayerInteractEntityC2SPacket.InteractTypeHandler action);

    void setVec(Vec3d vec3d);

    void setHand(Hand hand);

    int getEntityID();

    PlayerInteractEntityC2SPacket.InteractTypeHandler getAction();

    Vec3d getHitVec();


    Entity getAttackedEntity();

}
