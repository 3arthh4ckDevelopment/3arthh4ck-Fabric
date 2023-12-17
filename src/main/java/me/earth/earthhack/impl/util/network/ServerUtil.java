package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class ServerUtil implements Globals
{

    private static final ModuleCache<PingSpoof> PING_SPOOF =
            Caches.getModule(PingSpoof.class);

    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public static void disconnectFromMC(String message)
    {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection != null)
        {
            connection.getConnection().disconnect(Text.of(message));
        }
    }


    public static int getPingNoPingSpoof()
    {
        int ping = getPing();
        if (PING_SPOOF.isEnabled())
        {
            ping -= PING_SPOOF.get().getDelay();
        }

        return ping;
    }

    public static int getPing()
    {
        // if (PINGBYPASS.isEnabled() && !PingBypass.isServer())
        // {
        //     return PINGBYPASS.get().getServerPing();
        // }

        try
        {
            ClientPlayNetworkHandler connection = mc.getNetworkHandler();
            if (connection != null)
            {
                PlayerListEntry info = connection
                        .getPlayerListEntry(mc.getNetworkHandler()
                                         .getProfile()
                                         .getId());
                //noinspection ConstantConditions
                if (info != null)
                {
                    return info.getLatency();
                }
            }
        }
        catch (Throwable t)
        {
            // This can be called asynchronously so better be safe
            t.printStackTrace();
        }

        return 0;
    }

}
