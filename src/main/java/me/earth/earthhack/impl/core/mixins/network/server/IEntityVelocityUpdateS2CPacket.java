package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface IEntityVelocityUpdateS2CPacket 
{
    @Accessor(value = "entityId")
    int getId();

    @Accessor(value = "velocityX")
    int getX();

    @Accessor(value = "velocityX")
    void setX(int velocityX);

    @Accessor(value = "velocityY")
    int getY();

    @Accessor(value = "velocityY")
    void setY(int velocityY);

    @Accessor(value = "velocityZ")
    int getZ();

    @Accessor(value = "velocityZ")
    void setZ(int velocityZ);
}
