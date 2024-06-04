package me.earth.earthhack.impl.core.mixins.network;

import me.earth.earthhack.impl.core.ducks.network.INetworkState;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(NetworkState.PacketHandlerInitializer.class)
public abstract class MixinNetworkState implements INetworkState {

    @Unique private final Set<Class<? extends Packet<?>>> PACKETS = new HashSet<>();

    @Override
    public Set<Class<? extends Packet<?>>> earthhack$getPackets() {
        return PACKETS;
    }

    @Inject(method = "createSideToHandlerMap", at = @At(value = "RETURN"))
    public void createSideToHandlerMapHook(NetworkState state,
                                           CallbackInfoReturnable<Map<NetworkSide, NetworkState.PacketHandler<?>>> cir)
    {
        Map<NetworkSide, NetworkState.PacketHandler<?>> map = cir.getReturnValue();

        for (Map.Entry<NetworkSide, NetworkState.PacketHandler<?>> entry : map.entrySet()) {
            NetworkState.PacketHandler<?> handler = entry.getValue();
            printPacketHandler(handler);
        }
    }

    @Unique
    private void printPacketHandler(NetworkState.PacketHandler<?> handler) {
        Int2ObjectMap<Class<? extends Packet<?>>> packetMap = handler.getPacketIdToPacketMap();

        packetMap.forEach((id, packetClass) -> {
            PACKETS.add(packetClass);
            System.out.println("  ID: " + id + ", Packet: " + packetClass.getName());
        });
    }


}
