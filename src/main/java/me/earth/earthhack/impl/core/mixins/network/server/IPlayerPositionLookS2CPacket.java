package me.earth.earthhack.impl.core.mixins.network.server;

// PlayerPositionLookS2CPacket

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(PlayerPositionLookS2CPacket.class)
public interface IPlayerPositionLookS2CPacket
{
    @Accessor(value = "teleportId")
    int getTeleportId();

    @Accessor(value = "x")
    double getX();

    @Accessor(value = "y")
    double getY();

    @Accessor(value = "z")
    double getZ();

    @Accessor(value = "yaw")
    void setYaw(float yaw);

    @Accessor(value = "pitch")
    void setPitch(float pitch);
}
