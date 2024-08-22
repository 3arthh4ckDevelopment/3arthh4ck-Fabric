package me.earth.earthhack.impl.core.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.IClientConnection;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.packetdelay.PacketDelay;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.MutableText;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection
{

    // @Unique
    // private static final ModuleCache<Logger> LOGGER_MODULE =
    //         Caches.getModule(Logger.class);
    @Unique
    private static final ModuleCache<PacketDelay> PACKET_DELAY =
            Caches.getModule(PacketDelay.class);

    @Shadow public abstract boolean isOpen();

    @Shadow public abstract void flush();
    @Shadow public abstract void send(Packet<?> packet);
    @Shadow private PacketListener packetListener;
    @Shadow private Channel channel;

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "sendImmediately",
            at = @At("HEAD"),
            cancellable = true)
    private void earthhack$onSendPacket(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci)
    {
        earthhack$onSendPacket(packet, ci);
    }

    @Unique
    public void earthhack$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (PACKET_DELAY.isEnabled()
                && !PACKET_DELAY.get().packets.contains(packet)
                && PACKET_DELAY.get().isPacketValid(
                    FabricLoader.getInstance().getMappingResolver()
                        .unmapClassName("intermediate", packet.getClass().getName())))
        {
            ci.cancel();
            PACKET_DELAY.get().service.schedule(() ->
            {
                PACKET_DELAY.get().packets.add(packet);
                send(packet);
                PACKET_DELAY.get().packets.remove(packet);
            }, PACKET_DELAY.get().getDelay(), TimeUnit.MILLISECONDS);
            return;
        }

        PacketEvent.Send<?> event = getSendEvent(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }

    @Unique
    @Override
    public Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post)
    {
        // TODO: use PacketEvent.NoEvent instead!
        // if (LOGGER_MODULE.isEnabled()
        //         && LOGGER_MODULE.get().getMode() == LoggerMode.Normal)
        // {
        //     LOGGER_MODULE.get().logPacket(packet,
        //             "Sending (No Event) Post: " + post + ", ", false, true);
        // }

        PacketEvent.NoEvent<?> event = getNoEvent(packet, post);
        Bus.EVENT_BUS.post(event, packet.getClass());
        if (event.isCancelled())
        {
            return packet;
        }

        if (this.isOpen())
        {
            this.flush();

            if (post)
            {
                this.send(packet);
            }
            else
            {
                // this.dispatchSilently(packet);
            }

            return packet;
        }

        return null;
    }

    @Inject(
            method = "sendImmediately",
            at = @At("RETURN"))
    public void onSendPacketPost(Packet<?> packet,
                                 PacketCallbacks callbacks,
                                 boolean flush, CallbackInfo ci)
    {
        PacketEvent.Post<?> event = getPost(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());
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
            method = "disconnect(Lnet/minecraft/network/DisconnectionInfo;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;isOpen()Z"))
    public void onDisconnectHook(DisconnectionInfo disconnectionInfo, CallbackInfo ci)
    {
        if (this.isOpen())
        {
            Bus.EVENT_BUS.post(getDisconnect(MutableText.of(disconnectionInfo.reason().getContent())));
        }
    }

    // whatever, can't get the packet from NetworkState in 1.20
    /*
    @Unique
    private void dispatchSilently(Packet<?> inPacket)
    {
        final NetworkState enumconnectionstate =
                NetworkState.valueOf(inPacket);
        final NetworkState protocolConnectionState =
                this.channel.attr(ClientConnection.CLIENTBOUND_PROTOCOL_KEY).get().getState();

        if (protocolConnectionState != enumconnectionstate)
        {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (enumconnectionstate != protocolConnectionState)
            {
                ClientConnection.setHandlers(channel);
            }

            ChannelFuture channelfuture =
                    this.channel.writeAndFlush(inPacket);
            channelfuture.addListener(
                    ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(() ->
            {
                if (enumconnectionstate != protocolConnectionState)
                {
                    ClientConnection.setHandlers(channel);
                }

                ChannelFuture channelfuture1 =
                        channel.writeAndFlush(inPacket);
                channelfuture1.addListener(
                        ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }
    */

    @Inject(method = "exceptionCaught",
            at = @At("RETURN"))
    public void onExceptionCaught(ChannelHandlerContext p_exceptionCaught_1_,
                                  Throwable p_exceptionCaught_2_, CallbackInfo ci)
    {
        p_exceptionCaught_2_.printStackTrace();
        System.out.println("----------------------------------------------");
        Thread.dumpStack();
    }
}
