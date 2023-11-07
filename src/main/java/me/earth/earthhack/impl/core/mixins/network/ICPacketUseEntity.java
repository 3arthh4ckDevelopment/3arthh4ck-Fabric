package me.earth.earthhack.impl.core.mixins.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Duck Interface for {@link net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket}.
 */
public interface ICPacketUseEntity
{
    void setEntityId(int entityId);

    void setAction(PlayerActionC2SPacket.Action action);

    void setVec(Vec3d vec3d);

    void setHand(Hand hand);

    int getEntityID();

    PlayerActionC2SPacket.Action getAction();

    Vec3d getHitVec();

    Hand getHand();

    Entity getAttackedEntity();

}
