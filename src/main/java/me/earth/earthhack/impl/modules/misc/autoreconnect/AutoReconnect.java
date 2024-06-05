package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.gui.util.IDisconnectedScreen;
import me.earth.earthhack.impl.modules.misc.autoreconnect.util.ReconnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnect extends Module
{
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 5, 1, 60));

    private ServerInfo serverData;
    protected boolean connected;

    public AutoReconnect()
    {
        super("AutoReconnect", Category.Misc);
        this.listeners.add(new ListenerScreen(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.setData(new AutoReconnectData(this));
    }

    protected void setServerData()
    {
        ServerInfo data = mc.getCurrentServerEntry();
        if (data != null)
        {
            serverData = data;
        }
    }

    protected void onGuiDisconnected(DisconnectedScreen guiDisconnected)
    {
        Earthhack.getLogger().info("Automatically reconnecting...");
        setServerData();
        mc.setScreen(new ReconnectScreen(
                                    (IDisconnectedScreen) guiDisconnected,
                                    serverData,
                                    delay.getValue() * 1000));
    }

    public void setConnected(boolean connected) {
        Earthhack.getLogger().info(
            "AutoReconnect " + (connected ? "off" : "on"));
        this.connected = connected;
    }

}

