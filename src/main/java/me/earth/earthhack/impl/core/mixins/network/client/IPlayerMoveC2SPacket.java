package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacket {
    @Accessor(value = "x")
    void setX(double x);

    @Accessor(value = "y")
    void setY(double y);

    @Accessor(value = "z")
    void setZ(double z);

    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);

    @Accessor(value = "onGround")
    void setOnGround(boolean onGround);

    @Accessor(value = "changePosition")
    boolean isMoving();

    @Accessor(value = "changeLook")
    boolean isRotating();
}
