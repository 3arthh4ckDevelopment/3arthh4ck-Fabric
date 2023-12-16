package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Subscribing to all PlayerMoveC2SPackets is a tedious process
 * so this is a listener that makes that process easier.
 * Just add all the listeners to your Subscribers listeners
 * or subscribe this subscriber to the bus.
 */
public abstract class PlayerMoveC2SPacketListener extends SubscriberImpl
{
    public PlayerMoveC2SPacketListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public PlayerMoveC2SPacketListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Send<PlayerMoveC2SPacket>>
                (PacketEvent.Send.class, priority, PlayerMoveC2SPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Send<PlayerMoveC2SPacket> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround>>
                (PacketEvent.Send.class, priority, PlayerMoveC2SPacket.PositionAndOnGround.class)
        {
            @Override
            public void invoke(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround>>
                (PacketEvent.Send.class, priority, PlayerMoveC2SPacket.LookAndOnGround.class)
        {
            @Override
            public void invoke(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Send<PlayerMoveC2SPacket.Full>>
                (PacketEvent.Send.class,
                        priority,
                        PlayerMoveC2SPacket.Full.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Send<PlayerMoveC2SPacket> event);

    protected abstract void onPosition
            (PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event);

    protected abstract void onRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event);

    protected abstract void onPositionRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.Full> event);

}
