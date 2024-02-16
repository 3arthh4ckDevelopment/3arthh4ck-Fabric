package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(LoginHelloC2SPacket.class)
public interface ILoginHelloC2SPacket
{
    @Mutable
    @Accessor(value = "name")
    void setName(String name);

    @Mutable
    @Accessor(value = "profileId")
    void setUuid(UUID profileId);
}
