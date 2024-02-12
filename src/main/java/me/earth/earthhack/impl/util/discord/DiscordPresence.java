package me.earth.earthhack.impl.util.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.modules.client.rpc.LargeImage;
import me.earth.earthhack.impl.modules.client.rpc.RPC;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordPresence implements Globals
{
    private static final Logger LOGGER = LogManager.getLogger(DiscordPresence.class);
    private static final DiscordRichPresence presence = new DiscordRichPresence();
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private final RPC module;
    private Thread thread;
    private int catCounterBig = 1;

    public void details() {
        if (!module.isCustom()) {
            if (module.logoBig.getValue() != LargeImage.Cats) {
                // normal
                presence.largeImageKey = module.logoBig.getValue().getName();
                presence.largeImageText = Earthhack.NAME + " " + Earthhack.VERSION;
            } else {
                // cats
                if (catCounterBig > 16)
                    catCounterBig = 1;
                catCounterBig++;
                presence.largeImageKey = "cat" + catCounterBig;
                presence.largeImageText = "EarthCat " + Earthhack.VERSION;
            }
        } else {
            // custom
            presence.largeImageKey = module.assetLarge.getValue();
            presence.largeImageText = module.assetLargeText.getValue();
            if (module.smallImage.getValue()) {
                presence.smallImageKey = module.assetSmall.getValue();
                presence.smallImageText = module.assetSmallText.getValue();
            }
        }
    }

    public DiscordPresence(RPC module)
    {
        this.module = module;
    }

    public synchronized void start()
    {
        if (thread != null)
        {
            thread.interrupt();
        }

        LOGGER.info("Initializing Discord RPC");
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize((module.isCustom() ? module.customId.getValue() : "1076164046249791628"), handlers, true, "");
        rpc.Discord_Register((module.isCustom() ? module.customId.getValue() : "1076164046249791628"), null);

        presence.startTimestamp = System.currentTimeMillis() / 1000L;

        rpc.Discord_UpdatePresence(DiscordPresence.presence);
        StopWatch stopWatch = new StopWatch();
        stopWatch.reset();
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(2000);
                }
                catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }

                rpc.Discord_RunCallbacks();

                if (stopWatch.passed(1000)) {
                    details();
                    presence.details = module.Line1.getValue();
                    presence.state = getLine2();

                    if (module.join.getValue()) {
                        presence.partyId = "id";
                        presence.joinSecret = "secret";
                        presence.partyMax = module.partyMax.getValue();
                        presence.partySize = 1;
                    }

                    rpc.Discord_UpdatePresence(presence);
                }
            }
        }, "RPC-Callback-Handler");
        stopWatch.reset();
        thread.setDaemon(true);
        thread.start();
    }

    public synchronized void stop() {
        LOGGER.info("Shutting down Discord RPC");
        if (thread != null && !thread.isInterrupted())
        {
            thread.interrupt();
            thread = null;
        }

        rpc.Discord_Shutdown();
    }

    private String getLine2() {
        if (module.isCustom() || !module.showIP.getValue())
            return module.Line2.getValue();

        if (mc.world != null) {
            if (mc.isIntegratedServerRunning())
                return "Playing Singleplayer";
            else if (mc.getServer() != null)
                return "Playing on " + mc.getServer().getServerIp() + "!";
        } else {
            if (mc.currentScreen instanceof TitleScreen)
                return "In the main menu";
            else if (mc.currentScreen instanceof MultiplayerScreen)
                return "In the server selector";
        }

        return "Not in-game";
    }
}
