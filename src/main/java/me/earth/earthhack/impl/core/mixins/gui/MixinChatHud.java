package me.earth.earthhack.impl.core.mixins.gui;

import com.google.common.collect.Lists;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.debug.ConsoleColors;
import me.earth.earthhack.impl.modules.client.debug.Debug;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.util.collection.ArrayListDeque;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ListIterator;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud
{
    @Unique
    private static final ModuleCache<Chat> CHAT
            = Caches.getModule(Chat.class);
    @Unique
    private static final ModuleCache<Media> MEDIA
            = Caches.getModule(Media.class);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS
            = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Chat> RAINBOW
            = Caches.getSetting(Chat.class, BooleanSetting.class, "RainbowTimeStamps", false);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Chat> CLEAN
            = Caches.getSetting(Chat.class, BooleanSetting.class, "Clean", false);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Chat> INFINITE
            = Caches.getSetting(Chat.class, BooleanSetting.class, "Infinite", false);
    @Unique
    private static final SettingCache<ConsoleColors, Setting<ConsoleColors>, Debug> CONSOLE_COLORS
            = Caches.getSetting(Debug.class, EnumSetting.class, "ConsoleColors", ConsoleColors.Unformatted);

    @Shadow public abstract int getWidth();
    @Shadow protected abstract boolean isChatFocused();
    @Shadow public abstract double getChatScale();
    @Shadow protected abstract void refresh();

    @Final @Shadow private MinecraftClient client;
    @Unique @Final private final List<ChatHudLine> messages = Lists.newArrayList();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Unique @Final private final ArrayListDeque<String> messageHistory = new ArrayListDeque<>(100);
    @Final @Shadow private final List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();

    @Override
    @Invoker("addMessage")
    public abstract void earthhack$invokeAddMessage(Text text, @Nullable MessageSignatureData sig, int addedTime,
                                                               @Nullable MessageIndicator indicator, boolean refresh);

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
    public void addMessage(Text message, CallbackInfo info)
    {
        ChatEvent.Send event = new ChatEvent.Send((IChatHud) client.inGameHud.getChatHud(),
                Text.literal(message.getString()), client.inGameHud.getChatHud().getVisibleLineCount(), client.inGameHud.getTicks(), true);
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

    @Override
    public void earthhack$remove(@Nullable MessageSignatureData signature, boolean all) {
        if (signature == null)
            return;

        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        boolean changed = false;
        while (listIterator.hasNext()) {
            ChatHudLine message = listIterator.next();
            if (signature.equals(message.signature())) {
                listIterator.remove();
                changed = true;
                if (!all) {
                    break;
                }
            }
        }

        if (changed) {
            this.refresh();
        }
    }

    @Override
    @Accessor(value = "scrolledLines")
    public abstract int earthhack$getScrollPos();

    @Override
    @Accessor(value = "scrolledLines")
    public abstract void earthhack$setScrollPos(int pos);
    @Override
    @Accessor(value = "hasUnreadNewMessages")
    public abstract boolean earthhack$getScrolled();

    @Override
    @Accessor(value = "hasUnreadNewMessages")
    public abstract void earthhack$setScrolled(boolean scrolled);


    @Override
    public void earthhack$invokeClearChat(boolean sent)
    {
        this.visibleMessages.clear();
        this.messages.clear();

        if (sent)
        {
            this.messageHistory.clear();
        }
    }
}
