package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ExplosionS2CPacket.class)
public interface IExplosionS2CPacket
{
    @Mutable
    @Accessor(value = "playerVelocityX")
    void setX(float x);

    @Mutable
    @Accessor(value = "playerVelocityY")
    void setY(float y);

    @Mutable
    @Accessor(value = "playerVelocityZ")
    void setZ(float z);

    @Accessor(value = "playerVelocityX")
    float getX();

    @Accessor(value = "playerVelocityY")
    float getY();

    @Accessor(value = "playerVelocityZ")
    float getZ();

}
