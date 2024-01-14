package me.earth.earthhack.impl.modules.client.rpc;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.util.discord.DiscordPresence;

public class RPC extends Module {

    private final Setting<Page> page =
            register(new EnumSetting<>("Mode", Page.Default));

    /* ---------------- Default RPC -------------- */
    public final Setting<LargeImage> logoBig =
            register(new EnumSetting<>("LargeLogo", LargeImage.Phobos));

    /* ---------------- Common settings -------------- */
    public final Setting<String> Line1 =
            register(new StringSetting("Line1", "3arthh4ck"));
    public final Setting<String> Line2 =
            register(new StringSetting("Line2", ""));
    public final Setting<Boolean> showIP =
            register(new BooleanSetting("ShowIP", false));
    public final Setting<Boolean> join =
            register(new BooleanSetting("JoinButton", false));
    public final Setting<Integer> partyMax =
            register(new NumberSetting<>("MaxParty", 5, 1, 15));

    /* ---------------- Custom RPC -------------- */
    protected final Setting<Boolean> custom =
            register(new BooleanSetting("Custom", false));
    public final Setting<String> customId =
            register(new StringSetting("CustomId", "Application ID"));
    public final Setting<String> assetLarge =
            register(new StringSetting("LargeImage", "Large Asset Name"))
                    .setComplexity(Complexity.Expert);
    public final Setting<String> assetLargeText =
            register(new StringSetting("LargeImageText", "Large Asset Text"))
                    .setComplexity(Complexity.Expert);
    public final Setting<Boolean> smallImage =
            register(new BooleanSetting("SmallImageSetting", false));
    public final Setting<String> assetSmall =
            register(new StringSetting("SmallImage", "Small Asset Name"))
                    .setComplexity(Complexity.Expert);
    public final Setting<String> assetSmallText =
            register(new StringSetting("SmallImageText", "Small Asset Text"))
                    .setComplexity(Complexity.Expert);


    private DiscordPresence presence;

    public RPC() {
        super("RPC", Category.Client);
        this.setData(new RPCData(this));

        new PageBuilder<>(this, page)
                .addPage(p -> p == Page.Default, logoBig, partyMax)
                .addPage(p -> p == Page.Settings, Line1, partyMax)
                .addPage(p -> p == Page.Custom, custom, assetSmallText)
                .register(Visibilities.VISIBILITY_MANAGER);

        this.listeners.add(new LambdaListener<>(ShutDownEvent.class, e -> {
            if (presence != null)
                presence.stop();
        }));
    }

    @Override
    protected void onEnable() {
        if (osCheck()) {
            presence = new DiscordPresence(this);
            presence.start();
        } else {
            disable();
        }
    }

    @Override
    protected void onDisable() {
        if (presence != null)
            presence.stop();
    }

    private boolean osCheck() {
        String os = System.getProperty("os.version").toLowerCase();
        return !os.contains("android") && !os.contains("ios");
    }

    public boolean isCustom() {
        return page.getValue() == Page.Custom && custom.getValue() && customId.getValue().trim().matches("\\d+");
    }

    private enum Page {
        Default,
        Settings,
        Custom
    }

}
