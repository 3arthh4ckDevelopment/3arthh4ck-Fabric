package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPackets extends CPacketPlayerListener {

    private final Phase module;

    public ListenerCPackets(Phase module) {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event) {
        module.onPacket(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event) {
        module.onPacket(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event) {
        module.onPacket(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event) {
        module.onPacket(event);
    }
}