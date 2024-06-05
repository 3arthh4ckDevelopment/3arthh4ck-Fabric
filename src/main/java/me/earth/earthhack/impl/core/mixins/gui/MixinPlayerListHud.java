package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.stream.Stream;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {
    @Unique
    private static final ModuleCache<ExtraTab> EXTRA_TAB =
            Caches.getModule(ExtraTab.class);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, ExtraTab>
            PING = Caches.getSetting(ExtraTab.class, BooleanSetting.class,
            "Ping", false);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, ExtraTab>
            BARS = Caches.getSetting(ExtraTab.class, BooleanSetting.class,
            "Bars", true);

    @Shadow @Final private MinecraftClient client;
    @Unique private int maxPingOffset;
    @Shadow @Final private static Comparator<PlayerListEntry> ENTRY_ORDERING;

    // @SuppressWarnings({"unchecked", "rawtypes"})
    // @Redirect(
    //         method = "collectPlayerEntries()Ljava/util/List;",
    //         at = @At(
    //                 value = "INVOKE",
    //                 target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;",
    //                 remap = false))
    // public Stream<PlayerListEntry> collectPlayerEntriesHook(Stream instance, long l)
    // {
    //     // I have a feeling this won't work...
    //     return instance.sorted(ENTRY_ORDERING).limit(EXTRA_TAB.returnIfPresent(e ->
    //             Math.min(e.getSize((int) l), 80), l).longValue());
    // }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderPlayerlistHeadHook(DrawContext context,
                                          int scaledWindowWidth,
                                          Scoreboard scoreboard,
                                          @Nullable ScoreboardObjective objective,
                                          CallbackInfo ci)
    {
        if (PING.getValue())
        {
            maxPingOffset = this.client.getNetworkHandler()
                    .getPlayerList()
                    .stream()
                    .map(PlayerListEntry::getLatency)
                    .map(String::valueOf)
                    .map(this.client.textRenderer::getWidth)
                    .max(Integer::compare)
                    .orElse(0)
                    + 1
                    + (BARS.getValue() ? 12 : 0);
        }
    }

    @Inject(
            method = "getPlayerName",
            at = @At("HEAD"),
            cancellable = true)
    public void getPlayerNameHook(PlayerListEntry playerInfo,
                                  CallbackInfoReturnable<String> info)
    {
        info.setReturnValue(EXTRA_TAB.returnIfPresent(e ->
                e.getName(playerInfo), getPlayerNameDefault(playerInfo)));
    }

    @Unique
    private String getPlayerNameDefault(PlayerListEntry info)
    {
        return info.getDisplayName() != null
                ? info.getDisplayName().getString()
                : Team.decorateName(
                    info.getScoreboardTeam(),
                    Text.of(info.getProfile().getName()))
                        .getString();
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I",
                    ordinal = 0))
    private int getStringWidthHook(TextRenderer instance, StringVisitable text)
    {
        return PING.getValue()
                ? instance.getWidth(text) + maxPingOffset
                : instance.getWidth(text);
    }

    @Inject(method = "renderLatencyIcon",
            at = @At("HEAD"),
            cancellable = true)
    private void drawPingHook(DrawContext context, int width,
                              int x, int y, PlayerListEntry networkPlayerInfoIn,
                              CallbackInfo ci)
    {
        if (PING.getValue())
        {
            int color = networkPlayerInfoIn.getLatency() < 50
                    ? 0xFF00FF00
                    : networkPlayerInfoIn.getLatency() < 100
                    ? 0xFFFFFF00
                    : 0xFFFF0000;

            String toDraw = String.valueOf(networkPlayerInfoIn.getLatency());
            context.drawTextWithShadow(
                    this.client.textRenderer,
                    toDraw,
                    x + width - (BARS.getValue() ? 12 : 0)
                            - this.client.textRenderer.getWidth(toDraw),
                    y,
                    color);
        }

        if (!BARS.getValue())
        {
            ci.cancel();
        }
    }
}
