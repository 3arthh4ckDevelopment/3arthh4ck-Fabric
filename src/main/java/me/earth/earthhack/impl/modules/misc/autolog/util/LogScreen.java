package me.earth.earthhack.impl.modules.misc.autolog.util;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

import java.io.IOException;

public class LogScreen extends Screen implements Globals
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    private final AutoLog autoLog;
    private final ServerInfo data;
    private final String message;
    private GuiButton autoLogButton;
    private final int textHeight;

    public LogScreen(AutoLog autoLog, String message, ServerInfo data)
    {
        super(Text.of("GuiDisconnect"));
        this.autoLog = autoLog;
        this.message = message;
        this.data = data;
        this.textHeight = mc.fontRenderer.FONT_HEIGHT;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) { }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + mc.fontRenderer.FONT_HEIGHT, this.height - 30), (data == null ? TextColor.RED : TextColor.WHITE) + "Reconnect"));
        autoLogButton = new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 23, getButtonString());
        this.buttonList.add(autoLogButton);
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + mc.fontRenderer.FONT_HEIGHT, this.height - 30) + 46, "Back to server list"));
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0 && data != null)
        {
            if (PINGBYPASS.isEnabled())
            {
                mc.displayGuiScreen(new GuiConnectingPingBypass(new GuiMainMenu(), mc, data));
            }
            else
            {
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, data));
            }
        }
        else if (button.id == 1)
        {
            autoLog.toggle();
            autoLogButton.displayString = getButtonString();
        }
        else if (button.id == 2)
        {
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 0xffffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getButtonString()
    {
        return "AutoLog: " + (autoLog.isEnabled() ? TextColor.GREEN + "On" : TextColor.RED + "Off");
    }

}
