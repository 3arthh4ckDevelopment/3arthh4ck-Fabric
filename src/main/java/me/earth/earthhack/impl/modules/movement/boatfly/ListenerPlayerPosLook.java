package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPlayerPosLook extends
        ModuleListener<BoatFly, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPlayerPosLook(BoatFly module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.noForceRotate.getValue()
                && mc.player.getVehicle() != null
                && !(mc.currentScreen instanceof TitleScreen
                || mc.currentScreen instanceof DisconnectedScreen
                || mc.currentScreen instanceof DownloadingTerrainScreen
                || mc.currentScreen instanceof ConnectScreen
                || mc.currentScreen instanceof MultiplayerScreen))
        {
            event.setCancelled(true);
        }
    }

}