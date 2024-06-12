package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPackets extends CPacketPlayerListener {

    private final BoatFly module;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public ListenerCPackets(BoatFly module) {
        this.module = module;
    }


    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event) {
        if (module.noPosUpdate.getValue()
                && mc.player.getVehicle() != null)
        {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event) {
        if (module.noPosUpdate.getValue()
                && mc.player.getVehicle() != null)
        {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event) {
        if (module.noPosUpdate.getValue()
                && mc.player.getVehicle() != null)
        {
            event.setCancelled(true);
        }
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event) {
        if (module.noPosUpdate.getValue()
                && mc.player.getVehicle() != null)
        {
            event.setCancelled(true);
        }
    }

}