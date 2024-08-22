package me.earth.earthhack.impl.core.mixins.network.server;

import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityStatusS2CPacket.class)
public interface IEntityStatusS2CPacket
{
    @Accessor("entityId")
    int getEntityId();

    @Accessor("status")
    byte getLogicOpcode();
}
