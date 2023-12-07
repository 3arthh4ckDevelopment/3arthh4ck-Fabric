package me.earth.earthhack.impl.modules.misc.autolog.util;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class LogScreen extends Screen implements Globals
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    private final AutoLog autoLog;
    private final ServerInfo data;
    private final String message;
    private final int textHeight;

    public LogScreen(AutoLog autoLog, String message, ServerInfo data)
    {
        super(Text.of("GuiDisconnect"));
        this.autoLog = autoLog;
        this.message = message;
        this.data = data;
        this.textHeight = mc.textRenderer.fontHeight;

    }

    @Override
    public void init()
    {
        if (this.client != null) {
            clearChildren();

            addSelectableChild(ButtonWidget.builder(Text.of((data == null ? TextColor.RED : TextColor.WHITE) + "Reconnect"), (button) -> {
                if (data != null && this.client != null)
                    this.client.setScreen(new ConnectScreen(new TitleScreen(), Text.of("Failed to reconnect")));
                /*
                if (PINGBYPASS.isEnabled())
                {
                    mc.displayGuiScreen(new GuiConnectingPingBypass(new GuiMainMenu(), mc, data));
                }
                else
                {
                    mc.setScreen(new ConnectScreen(new TitleScreen(), "Failed to reconnect"));
                }
                 */
            }).dimensions(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + textHeight, this.height - 30), 200, 20).build());

            addSelectableChild(ButtonWidget.builder(Text.of(getButtonString()), (button) -> {
                autoLog.toggle();
                button.setMessage(Text.of(getButtonString()));
            }).dimensions(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + textHeight, this.height - 30) + 23, 200, 20).build());


            addSelectableChild(ButtonWidget.builder(Text.of("Back to server list"), (button) -> {
                this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
            }).dimensions(this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + textHeight, this.height - 30) + 46, 200, 20).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        // this.drawDefaultBackground();
        // this.drawCenteredString(this.textRenderer, this.message, this.width / 2, this.height / 2 - this.textHeight / 2 - this.textHeight * 2, 0xffffffff);
        super.render(context, mouseX, mouseY, partialTicks);
    }

    private String getButtonString()
    {
        return "AutoLog: " + (autoLog.isEnabled() ? TextColor.GREEN + "On" : TextColor.RED + "Off");
    }

}
