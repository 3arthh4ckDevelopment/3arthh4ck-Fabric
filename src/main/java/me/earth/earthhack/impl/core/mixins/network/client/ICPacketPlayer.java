package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface ICPacketPlayer
{
    @Mutable
    @Accessor(value = "x")
    void setX(double x);

    @Mutable
    @Accessor(value = "y")
    void setY(double y);

    @Mutable
    @Accessor(value = "z")
    void setZ(double z);

    @Mutable
    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Mutable
    @Accessor(value = "pitch")
    void setPitch(float pitch);

    @Mutable
    @Accessor(value = "onGround")
    void setOnGround(boolean onGround);

    @Mutable
    @Accessor(value = "changePosition")
    boolean isMoving();

    @Mutable
    @Accessor(value = "changeLook")
    boolean isRotating();

}