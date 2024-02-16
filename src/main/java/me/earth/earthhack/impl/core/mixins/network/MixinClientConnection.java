package me.earth.earthhack.impl.core.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.IClientConnection;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection
{
    @Shadow public abstract boolean isOpen();
    @Shadow private PacketListener packetListener;

    @Inject(method = "sendImmediately",
            at = @At("HEAD"),
            cancellable = true)
    private void earthhack$onSendPacket(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci)
    {
        earthhack$onSendPacket(packet, ci);
    }

    @Unique
    public void earthhack$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        // if (PACKET_DELAY.isEnabled()
        //         && !PACKET_DELAY.get().packets.contains(packet)
        //         && PACKET_DELAY.get().isPacketValid(
        //         MappingProvider.simpleName(packet.getClass())))
        // {
        //     ci.cancel();
        //     PACKET_DELAY.get().service.schedule(() ->
        //     {
        //         PACKET_DELAY.get().packets.add(packet);
        //         sendPacket(packet);
        //         PACKET_DELAY.get().packets.remove(packet);
        //     }, PACKET_DELAY.get().getDelay(), TimeUnit.MILLISECONDS);
        //     return;
        // }

        PacketEvent.Send<?> event = getSendEvent(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;handlePacket" +
                            "(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    @SuppressWarnings("unchecked")
    private void onPacketReceive(ChannelHandlerContext context,
                                 Packet<?> packet,
                                 CallbackInfo info)
    {
        PacketEvent.Receive<?> event = getReceive(packet);

        try
        {
            Bus.EVENT_BUS.post(event, packet.getClass());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

        if (event.isCancelled())
        {
            info.cancel();
        }
        else if (!event.getPostEvents().isEmpty())
        {
            try
            {
                ((Packet<PacketListener>) packet)
                        .apply(this.packetListener);
            }
            catch (Exception e) // ThreadQuickExitException
            {
                // Could use @Redirect instead, but @Inject breaks less
            }

            for (Runnable runnable : event.getPostEvents())
            {
                // TODO: check that this fix didn't break anything (probably didn't)
                // Scheduler.getInstance().scheduleAsynchronously(runnable);
                MinecraftClient.getInstance().execute(runnable);
            }

            info.cancel();
        }
    }

    @Inject(
            method = "disconnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;isOpen()Z",
                    remap = false))
    public void onDisconnectHook(Text component, CallbackInfo info)
    {
        if (this.isOpen())
        {
            Bus.EVENT_BUS.post(getDisconnect(MutableText.of(component.getContent())));
        }
    }
}
