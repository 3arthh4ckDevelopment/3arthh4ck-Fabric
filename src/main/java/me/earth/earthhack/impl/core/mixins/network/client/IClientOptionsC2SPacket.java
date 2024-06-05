package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientOptionsC2SPacket.class)
public interface IClientOptionsC2SPacket {
    @Accessor("options")
    void earthhack$setOptions(SyncedClientOptions options);
    @Accessor("options")
    SyncedClientOptions earthhack$getOptions();
}
