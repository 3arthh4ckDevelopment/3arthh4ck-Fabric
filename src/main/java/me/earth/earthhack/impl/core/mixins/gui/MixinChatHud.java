package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import net.fabricmc.fabric.api.registry.LootEntryTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.main.Main;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class MixinChatHud {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(
        method = "clear",
        at = @At("HEAD"),
        cancellable = true)
    public void clear(boolean sent, CallbackInfo info){
        ChatEvent.Clear event = new ChatEvent.Clear(sent);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V",
            at = @At("HEAD"),
            cancellable = true)
    public void addMessage(Text message, CallbackInfo info) {
        ChatEvent.Send event = new ChatEvent.Send((IChatHud) client.inGameHud.getChatHud(), Text.literal(message.getString()), 0, 0, true);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(method = "logChatMessage",
            at = @At("HEAD"),
            cancellable = true)
    public void logChatMessage(Text message, MessageIndicator indicator, CallbackInfo info){
        ChatEvent.Log event = new ChatEvent.Log(message.getString());
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

}
