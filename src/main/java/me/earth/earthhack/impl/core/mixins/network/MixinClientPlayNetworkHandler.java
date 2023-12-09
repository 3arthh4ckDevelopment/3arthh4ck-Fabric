package me.earth.earthhack.impl.core.mixins.network;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayNetworkHandler;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler implements IClientPlayNetworkHandler {

    @Final
    @Shadow
    private MinecraftClient client;

    @Override
    public boolean isDoneLoadingTerrain() {
        return false;
    }

    @Override
    public void setDoneLoadingTerrain(boolean loaded) {

    }

    @Override
    @Accessor(value = "profile")
    public abstract void setGameProfile(GameProfile gameProfile);

    @Inject(method = "sendChatMessage",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void sendChatMessage(String content, CallbackInfo info) {
        ChatEvent.Send event = new ChatEvent.Send(null, Text.literal(content), 0, 0, false);
        Bus.EVENT_BUS.post(event);
        if(event.isCancelled())
            info.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V",
        at = @At("HEAD"),
        cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        /*
        if (PACKET_DELAY.isEnabled()
                && !PACKET_DELAY.get().packets.contains(packet)
                && PACKET_DELAY.get().isPacketValid(
                MappingProvider.simpleName(packet.getClass())))
        {
            ci.cancel();
            PACKET_DELAY.get().service.schedule(() ->
            {
                PACKET_DELAY.get().packets.add(packet);
                sendPacket(packet);
                PACKET_DELAY.get().packets.remove(packet);
            }, PACKET_DELAY.get().getDelay(), TimeUnit.MILLISECONDS);
            return;
        }
        */
        PacketEvent.Send<?> event = getSendEvent(packet);
        Bus.EVENT_BUS.post(event, packet.getClass());

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }



}
