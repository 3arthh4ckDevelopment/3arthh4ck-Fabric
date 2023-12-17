package me.earth.earthhack.impl.core.mixins.gui;

import com.google.common.collect.Lists;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.core.ducks.gui.IChatHudLine;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.debug.ConsoleColors;
import me.earth.earthhack.impl.modules.client.debug.Debug;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.misc.collections.ConvenientStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

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

    @Shadow
    public abstract int getWidth();

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract double getChatScale();

    @Final @Shadow private MinecraftClient client; //TODO: is this variable needed?
    @Final @Shadow private List<ChatHudLine> messages = Lists.newArrayList();
    @Final @Shadow private List<String> messageHistory = Lists.newArrayList();
    @Final @Shadow private List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();

    @Shadow
    protected abstract void addMessage(Text message,
                                       @Nullable MessageSignatureData signature,
                                       int ticks,
                                       @Nullable MessageIndicator indicator,
                                       boolean refresh);


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
                Text.literal(message.getString()), client.inGameHud.getChatHud().getVisibleLineCount(), 0, true);
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
    public boolean replace(MutableText component, int id, boolean wrap, boolean returnFirst) {
        boolean set;
        set = setLine(component, id, messages, wrap, returnFirst);
        set = setLine(component, id, visibleMessages, wrap, returnFirst) || set;
        return set;
    }


    boolean setLine(Text component,
                    int id,
                    List<ChatHudLine> list,
                    boolean wrap,
                    boolean returnFirst)
    {
        Stack<Text> wrapped = null;
        if (wrap)
        {
            int max = MathHelper.floor(getWidth() / getChatScale());
            wrapped = new ConvenientStack<>(GuiUtilRenderComponents
                    .splitText(component, max, client.textRenderer, false, false));
        }

        int last = 0;
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            ChatHudLine line = list.get(i);
            if (line.creationTick() == id)
            {
                if (wrap)
                {
                    Text itc = wrapped.pop();
                    if (itc != null)
                    {
                        ((IChatHudLine) line).setComponent(itc);
                        last = i + 1;
                    }
                    else
                    {
                        toRemove.add(i);
                    }
                }
                else
                {
                    ((IChatHudLine) line).setComponent(component);
                    if (returnFirst)
                    {
                        return true;
                    }
                }
            }
        }

        if (toRemove.isEmpty())
        {
            boolean infinite = INFINITE.getValue();
            while (infinite && wrap && !wrapped.empty())
            {
                Text itc = wrapped.pop();
                if (itc != null)
                {
                    ChatHudLine newLine = new ChatHudLine(this.client.inGameHud.getTicks(), itc, null, new MessageIndicator(1, null, null, null));
                    CHAT.get().animationMap.put(newLine, new TimeAnimation(CHAT.get().time.getValue(), -(MinecraftClient.getInstance().textRenderer.getWidth(newLine.content().getString())), 0, false, AnimationMode.LINEAR));
                    list.add(last,
                            newLine);
                    last++;
                }
            }
        }
        else
        {
            toRemove.forEach(i -> list.set(i, null));
            list.removeIf(Objects::isNull);
        }

        return false;
    }

    @Override
    @Accessor(value = "scrolledLines")
    public abstract int getScrollPos();

    @Override
    @Accessor(value = "scrolledLines")
    public abstract void setScrollPos(int pos);
    @Override
    @Accessor(value = "hasUnreadNewMessages")
    public abstract boolean getScrolled();

    @Override
    @Accessor(value = "hasUnreadNewMessages")
    public abstract void setScrolled(boolean scrolled);

    @Override
    public void invokeSetChatLine(Text chatComponent,
                                  int chatLineId,
                                  int updateCounter,
                                  boolean displayOnly)
    {
        this.addMessage(chatComponent, null, updateCounter, null, displayOnly);
    }

    @Override
    public void invokeClearChat(boolean sent)
    {
        this.visibleMessages.clear();
        this.messages.clear();

        if (sent)
        {
            this.messageHistory.clear();
        }
    }
}
