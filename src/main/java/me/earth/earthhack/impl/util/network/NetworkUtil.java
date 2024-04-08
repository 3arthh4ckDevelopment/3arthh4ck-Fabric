package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IClientConnection;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class NetworkUtil implements Globals
{
    /**
     * Sends the given Packet safely (Packet won't be sent
     * if {@link MinecraftClient#getNetworkHandler()} ()} is <tt>null</tt>).
     *
     * @param packet the packet to send.
     */
    public static void send(Packet<?> packet)
    {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection != null)
        {
            connection.sendPacket(packet);
        }
    }

    /**
     * Convenience Method, calls
     * {@link IClientConnection#sendPacketNoEvent(Packet)}.
     *
     * @param packet the packet to send.
     * @return the packet or null if failed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Packet<?> sendPacketNoEvent(Packet<?> packet)
    {
        return sendPacketNoEvent(packet, true);
    }

    /**
     * Convenience Method, calls
     * {@link IClientConnection#sendPacketNoEvent(Packet, boolean)}.
     *
     * @param packet the packet to send.
     * @param post if a post event should be sent.
     * @return the packet or null if failed.
     */
    public static Packet<?> sendPacketNoEvent(Packet<?> packet, boolean post)
    {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection != null)
        {
            IClientConnection manager =
                    (IClientConnection) connection.getConnection();

            return manager.sendPacketNoEvent(packet, post);
        }

        return null;
    }

    public static boolean receive(Packet<ClientPlayPacketListener> packet)
    {
        if (mc.player != null) {
            return receive(packet, mc.player.networkHandler.getConnection());
        }

        return false;
    }

    public static boolean receive(Packet<ClientPlayPacketListener> packet, ClientConnection manager)
    {
        PacketEvent.Receive<?> e = new PacketEvent.Receive<>(packet, manager);
        Bus.EVENT_BUS.post(e, packet.getClass());
        if (e.isCancelled())
        {
            return false;
        }

        packet.apply(mc.getNetworkHandler());

        for (Runnable runnable : e.getPostEvents())
        {
            runnable.run();
        }

        return true;
    }

}
