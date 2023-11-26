package me.earth.earthhack.impl.core.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.channels.Channel;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    private Channel channel;
    @Shadow @Final
    private NetworkSide side;

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo ci) {
        if (this.channel.isOpen() && packet != null) {
            try {
                PacketEvent.Receive<?> event = new PacketEvent.Receive<>(packet, MinecraftClient.getInstance().getNetworkHandler());
                Bus.EVENT_BUS.post(event);
                if (event.isCancelled())
                    ci.cancel();
            } catch (Exception ignored) { }
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (this.side != NetworkSide.CLIENTBOUND) return;
        try {
            PacketEvent.Send<?> event = new PacketEvent.Send<>(packet);
            Bus.EVENT_BUS.post(event);
            if (event.isCancelled()) ci.cancel();
        } catch (Exception ignored) { }
    }


}
