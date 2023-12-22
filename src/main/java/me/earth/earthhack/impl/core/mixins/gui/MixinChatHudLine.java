package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.core.ducks.gui.IChatHudLine;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.util.animation.AnimationMode;
import me.earth.earthhack.impl.util.animation.TimeAnimation;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHudLine.class)
public abstract class MixinChatHudLine implements IChatHudLine
{
    @Unique private static final ModuleCache<Chat> CHAT = Caches.getModule(Chat.class);
    @Unique private static final SettingCache<Boolean, BooleanSetting, Chat> TIME_STAMPS
                    = Caches.getSetting(Chat.class, BooleanSetting.class, "TimeStamps", false);
    @Unique private static final SettingCache<Color, ColorSetting, Chat> COLOR
                    = Caches.getSetting(Chat.class, ColorSetting.class, "TimeStampsColor", Color.WHITE);
    @Unique private static final SettingCache<Chat.Rainbow, EnumSetting<Chat.Rainbow>, Chat> RAINBOW
                    = Caches.getSetting(Chat.class, Setting.class, "Rainbow", Chat.Rainbow.None);
    @Unique private static final DateFormat FORMAT = new SimpleDateFormat("k:mm");
    @Unique private static final MinecraftClient MC = MinecraftClient.getInstance();

    @Unique private String timeStamp;


    @Override
    public String getTimeStamp()
    {
        return timeStamp;
    }
    @Override
    @Accessor(value = "content")
    public abstract void setComponent(Text component);


    @Inject(
            method = "<init>",
            at = @At("RETURN"))
    public void constructorHook(int creationTick,
                                Text lineStringIn,
                                MessageSignatureData messageSignatureData,
                                MessageIndicator messageIndicator,
                                CallbackInfo ci)
    {
        StringBuilder timeStampBuilder = new StringBuilder();
        switch (RAINBOW.getValue()) {
            case None -> {
                Color color = COLOR.getValue();
                int hex = MathUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                String colorString = TextColor.CUSTOM + Integer.toHexString(hex);
                timeStampBuilder.append(colorString);
            }
            case Horizontal -> timeStampBuilder.append(TextColor.RAINBOW_PLUS);
            case Vertical -> timeStampBuilder.append(TextColor.RAINBOW_MINUS);
        }

        this.timeStamp = timeStampBuilder.append("<")
                .append(FORMAT.format(new Date()))
                .append("> ")
                .append(TextColor.RESET)
                .toString();

        String t = lineStringIn.getString();
        if (CHAT.isEnabled() && TIME_STAMPS.getValue())
        {
            t = timeStamp + t;
        }

        CHAT.get().animationMap.put(
                ChatHudLine.class.cast(this),
                new TimeAnimation(
                        CHAT.get().time.getValue(),
                        -(MC.textRenderer.getWidth(t)),
                        0,
                        false,
                        AnimationMode.LINEAR));
    }
}
