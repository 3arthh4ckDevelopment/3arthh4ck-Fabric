package me.earth.earthhack.impl.core.mixins.network;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
        ChatEvent.Send event = new ChatEvent.Send((IChatHud) client.inGameHud.getChatHud(), Text.literal(content), 0, 0, false);
        Bus.EVENT_BUS.post(event);
        if(event.isCancelled())
            info.cancel();
    }

}
