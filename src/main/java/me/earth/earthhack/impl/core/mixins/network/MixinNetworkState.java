package me.earth.earthhack.impl.core.mixins.network;

// TODO, chinese asf
/*@Mixin(NetworkPhase.PacketHandlerInitializer.class)
public abstract class MixinNetworkState implements INetworkState {

    @Unique private final Set<Class<? extends Packet<?>>> PACKETS = new HashSet<>();

    @Override
    public Set<Class<? extends Packet<?>>> earthhack$getPackets() {
        return PACKETS;
    }

    @Inject(method = "createSideToHandlerMap", at = @At(value = "RETURN"))
    public void createSideToHandlerMapHook(NetworkPhase state,
                                           CallbackInfoReturnable<Map<NetworkSide, NetworkPhase.PacketHandler<?>>> cir)
    {
        Map<NetworkSide, NetworkPhase.PacketHandler<?>> map = cir.getReturnValue();

        for (Map.Entry<NetworkSide, NetworkPhase.PacketHandler<?>> entry : map.entrySet()) {
            NetworkPhase.PacketHandler<?> handler = entry.getValue();
            printPacketHandler(handler);
        }
    }

    @Unique
    private void printPacketHandler(NetworkPhase.PacketHandler<?> handler) {
        Int2ObjectMap<Class<? extends Packet<?>>> packetMap = handler.getPacketIdToPacketMap();

        packetMap.forEach((id, packetClass) -> {
            PACKETS.add(packetClass);
            //System.out.println("  ID: " + id + ", Packet: " + packetClass.getName());
        });
    }


}*/
