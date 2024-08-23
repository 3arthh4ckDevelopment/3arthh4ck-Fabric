package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;

public class ConnectCommand extends Command
        implements Globals, CommandScheduler
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
        Caches.getModule(PingBypassModule.class);

    private ServerList cachedServerList;
    private long lastCache;

    public ConnectCommand()
    {
        super(new String[][]{{"connect"}, {"ip"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length < 2)
        {
            ChatUtil.sendMessage(TextColor.RED + "Please specify an IP!");
            return;
        }

        // if (PINGBYPASS.isEnabled()
        //     && !PINGBYPASS.get().isOld()
        //     && mc.player != null)
        // {
        //     mc.player.networkHandler.sendPacket(new C2SCommandPacket(args));
        //     return;
        // }

        ServerAddress serveraddress = ServerAddress.parse(args[1]);
        // if (PingBypass.isConnected()) {
        //     try {
        //         PingBypass.sendPacket(new S2CUnloadWorldPacket(
        //             "Pingbypass is connecting to " + args[1] + "..."));
        //         PingBypass.DISCONNECT_SERVICE.setAllow(true);
        //         ServerUtil.disconnectFromMC("Disconnecting.");
        //     } finally {
        //         PingBypass.DISCONNECT_SERVICE.setAllow(false);
        //     }
        // }

        SCHEDULER.submit(() -> mc.execute(() -> {
            if (PINGBYPASS.isEnabled()) {
                // mc.setScreen(new GuiConnectingPingBypass(new GuiMainMenu(), mc, serveraddress.getIP(), serveraddress.getPort()));
            } else {
                ConnectScreen.connect(new TitleScreen(), mc, serveraddress,
                        new ServerInfo(args[1], serveraddress.getAddress(), ServerInfo.ServerType.OTHER), true, null);
            }
        }), 100);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        if (cachedServerList == null
                || System.currentTimeMillis() - lastCache > 60000)
        {
            cachedServerList = new ServerList(this.mc);
            cachedServerList.loadFile();
            lastCache = System.currentTimeMillis();
        }

        if (args.length >= 2)
        {
            for (int i = 0; i < cachedServerList.size(); i++)
            {
                ServerInfo data = cachedServerList.get(i);
                //noinspection PointlessNullCheck
                if (data.address != null
                    && TextUtil.startsWith(data.address, args[1]))
                {
                    return new PossibleInputs(TextUtil.substring(
                        data.address, args[1].length()), "");
                }
            }
        }

        if (args.length >= 2)
        {
            return PossibleInputs.empty();
        }

        return super.getPossibleInputs(args);
    }

}
