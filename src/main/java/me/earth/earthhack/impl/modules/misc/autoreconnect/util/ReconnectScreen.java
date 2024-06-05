package me.earth.earthhack.impl.modules.misc.autoreconnect.util;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.mixins.gui.util.IDisconnectedScreen;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.io.IOException;

public class ReconnectScreen extends DisconnectedScreen
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    /** Timer measuring handling the delay. */
    private final StopWatch timer = new StopWatch();
    /** Accessor for the parentGui to get Screen, Reason and Message from. */
    private final IDisconnectedScreen parent;
    /** The ServerData we reconnect to. */
    private final ServerInfo data;
    /** The delay until we reconnect. */
    private final int delay;
    /** A button to turn reconnecting on and off. */
    private ButtonWidget reconnectButton;
    /** Marks if no data is available. */
    private boolean noData;
    /** Marks if we should reconnect. */
    private boolean reconnect;
    /** Marks the time we stopped the timer */
    private long time;

    public ReconnectScreen(IDisconnectedScreen parent,
                           ServerInfo serverData,
                           int delay)
    {
        super(parent.getParentScreen(), parent.getReason(), parent.getMessage());
        this.parent    = parent;
        this.data      = serverData;
        this.delay     = delay;
        this.reconnect = true;
        this.time = System.currentTimeMillis();
        this.client = MinecraftClient.getInstance();
        timer.reset();
    }

    @Override
    public void init()
    {
        super.init();
        this.drawables.clear();
        int textHeight = ((IDisconnectedScreen) this).getMessage().getString().length() * this.textRenderer.fontHeight;
        this.drawables.add(new ButtonWidget.Builder(
                Text.of((data == null ? TextColor.RED : TextColor.WHITE) + "Reconnect"),
                o -> handleButtonPress(1))
                        .position(this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.textRenderer.fontHeight, this.height - 30))
                        .size(200, 20)
                        .build());
        this.reconnectButton = new ButtonWidget.Builder(
                Text.of(getButtonString()),
                o -> handleButtonPress(2))
                        .position(this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + client.textRenderer.fontHeight, this.height - 30) + 23)
                        .size(200, 20)
                        .build();
        this.drawables.add(reconnectButton);
        this.drawables.add(new ButtonWidget.Builder(
                Text.translatable("gui.toMenu"),
                o -> handleButtonPress(0))
                        .position(this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + client.textRenderer.fontHeight, this.height - 30) + 46)
                        .size(200, 20)
                        .build());
    }
    
    private void handleButtonPress(int id)
    {
        switch (id)
        {
            case 0 -> client.setScreen(parent.getParentScreen());
            case 1 -> connect();
            case 2 -> {
                reconnect = !reconnect;
                this.time = timer.getTime();
                this.reconnectButton.setMessage(Text.of(getButtonString()));
            }
        }
    }

    @Override
    public void tick()
    {
        if (!reconnect)
        {
            timer.setTime(System.currentTimeMillis() - time);
        }

        if (noData)
        {
            if (timer.passed(3000))
            {
                client.setScreen(new MultiplayerScreen(new TitleScreen()));
            }
        }
        else if (timer.passed(delay) && reconnect)
        {
            connect();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        super.render(context, mouseX, mouseY, partialTicks);
        String text = getReconnectString();
        Managers.TEXT.drawStringWithShadow(context, text,
                width / 2.0f - Managers.TEXT.getStringWidth(text) / 2.0f,
                16,
                0xffffffff);
    }

    private void connect()
    {
        ServerInfo serverData = data == null ? client.getCurrentServerEntry() : data;
        if (serverData != null)
        {
            // if (PINGBYPASS.isEnabled())
            // {
            //     client.setScreen(
            //             new GuiConnectingPingBypass(parent.getParentScreen(),
            //                                         mc,
            //                                         serverData));
            // }
            // else
            {
                ConnectScreen.connect(parent.getParentScreen(), client,
                        new ServerAddress(serverData.address, 25565), serverData, false);
            }
        }
        else
        {
            noData = true;
            timer.reset();
        }
    }

    private String getButtonString()
    {
        return "AutoReconnect: "
                + (reconnect ? TextColor.GREEN + "On" : TextColor.RED + "Off");
    }

    private String getReconnectString()
    {
        float time = MathUtil.round((delay -
                (reconnect ? timer.getTime() : this.time)) / 1000.0f, 1);
        return  noData
                    ? (TextColor.RED + "No ServerData found!")
                    : ("Reconnecting in " + (time <= 0 ? "0.0" : time) + "s.");
    }

}
