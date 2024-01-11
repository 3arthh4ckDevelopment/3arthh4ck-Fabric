package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Event;

/**
 * Fired when a {@link MotionUpdateEvent} has been
 * fired, but no {@link net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket} has been sent.
 */
public class NoMotionUpdateEvent extends Event
{

}
