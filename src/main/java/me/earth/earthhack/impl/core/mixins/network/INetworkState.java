package me.earth.earthhack.impl.core.mixins.network;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(NetworkState.class)
public interface INetworkState {
    @Accessor(value = "packetHandlers")
    Map<NetworkSide, NetworkState.PacketHandler<?>> getPacketHandlers();

}
