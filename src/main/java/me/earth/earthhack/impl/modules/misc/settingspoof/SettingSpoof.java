package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.util.Arm;

public class SettingSpoof extends Module
{
    protected final Setting<Boolean> spoofLanguage =
        register(new BooleanSetting("Spoof-Language", false));
    protected final Setting<String> language =
        register(new StringSetting("Language", "en_us"));
    protected final Setting<Boolean> spoofRender =
        register(new BooleanSetting("Spoof-RenderDistance", false));
    protected final Setting<Integer> renderDist =
        register(new NumberSetting<>("RenderDistance", 32, -1, 128));
    protected final Setting<Boolean> chatColors =
        register(new BooleanSetting("ChatColors", true));
    protected final Setting<Boolean> spoofChat =
        register(new BooleanSetting("Spoof-Chat", false));
    protected final Setting<ChatVisibilityTranslator> chat =
        register(new EnumSetting<>("Chat", ChatVisibilityTranslator.Full));
    protected final Setting<Boolean> chatFilter =
        register(new BooleanSetting("ChatFiltering", true));
    protected final Setting<Boolean> spoofModel =
        register(new BooleanSetting("Spoof-Model", false));
    protected final Setting<Integer> model =
        register(new NumberSetting<>("Model", 0, 0, 64));
    protected final Setting<Boolean> spoofHand =
        register(new BooleanSetting("Spoof-Hand", false));
    protected final Setting<HandTranslator> hand =
        register(new EnumSetting<>("Hand", HandTranslator.Right));
    protected final Setting<Boolean> allowServerList =
        register(new BooleanSetting("Spoof-AllowServerList", true));
    protected final Setting<Boolean> send =
        register(new BooleanSetting("Send", true));

    public SettingSpoof()
    {
        super("SettingSpoof", Category.Misc);
        this.listeners.add(new ListenerSettings(this));
        this.send.addObserver(e ->
        {
            e.setValue(true);
            sendPacket();
        });
    }

    public void sendPacket()
    {
        if (mc.player != null)
        {
            String lang =
                    getLanguage(mc.options.language);
            int render  =
                    getRenderDistance(mc.options.getViewDistance().getValue());
            ChatVisibility vis =
                    getVisibility(mc.options.getChatVisibility().getValue());
            boolean chatColors =
                    getChatColors(mc.options.getChatColors().getValue());
            boolean[] filterAndListing = {
                    getServerList(mc.options.getAllowServerListing().getValue()),
                    getChatFilter(mc.options.getSyncedOptions().filtersText())
            };

            int mask = 0;

            for (PlayerModelPart part
                    : mc.options.enabledPlayerModelParts)
            {
                mask |= part.getBitFlag();
            }

            int modelParts =
                    getModelParts(mask);
            Arm handSide =
                    getHandSide(mc.options.getMainArm().getValue());

            NetworkUtil.sendPacketNoEvent(
                new ClientOptionsC2SPacket(new SyncedClientOptions(
                    lang, render, vis, chatColors, modelParts, handSide, filterAndListing[0], filterAndListing[1])));
        }
    }

    public String getLanguage(String languageIn)
    {
        return spoofLanguage.getValue()
                ? language.getValue()
                : languageIn;
    }

    public int getRenderDistance(int renderDistIn)
    {
        return spoofRender.getValue()
                ? renderDist.getValue()
                : renderDistIn;
    }

    public ChatVisibility getVisibility(
            ChatVisibility enumChatVisibilityIn)
    {
        return spoofChat.getValue()
                ? chat.getValue().getVisibility()
                : enumChatVisibilityIn;
    }

    public boolean getChatColors(boolean chatColorsIn)
    {
        return spoofChat.getValue()
                ? chatColors.getValue()
                : chatColorsIn;
    }

    public boolean getChatFilter(boolean chatFilterIn)
    {
        return spoofChat.getValue()
                ? chatFilter.getValue()
                : chatFilterIn;
    }

    public boolean getServerList(boolean serverListIn)
    {
        return allowServerList.getValue()
                ? allowServerList.getValue()
                : serverListIn;
    }

    public int getModelParts(int modelPartsIn)
    {
        return spoofModel.getValue()
                ? model.getValue()
                : modelPartsIn;
    }

    public Arm getHandSide(Arm HandSideIn)
    {
        return spoofHand.getValue()
                ? hand.getValue().getHandSide()
                : HandSideIn;
    }

}
