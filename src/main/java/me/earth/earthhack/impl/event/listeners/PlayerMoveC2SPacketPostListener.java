package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public abstract class PlayerMoveC2SPacketPostListener extends SubscriberImpl
{
    public PlayerMoveC2SPacketPostListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public PlayerMoveC2SPacketPostListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Post<PlayerMoveC2SPacket>>
                (PacketEvent.Post.class, priority, PlayerMoveC2SPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Post<PlayerMoveC2SPacket> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<PlayerMoveC2SPacket.PositionAndOnGround>>
                (PacketEvent.Post.class, priority, PlayerMoveC2SPacket.PositionAndOnGround.class)
        {
            @Override
            public void invoke(PacketEvent.Post<PlayerMoveC2SPacket.PositionAndOnGround> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<PlayerMoveC2SPacket.LookAndOnGround>>
                (PacketEvent.Post.class, priority, PlayerMoveC2SPacket.LookAndOnGround.class)
        {
            @Override
            public void invoke(PacketEvent.Post<PlayerMoveC2SPacket.LookAndOnGround> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Post<PlayerMoveC2SPacket.Full>>
                (PacketEvent.Post.class,
                        priority,
                        PlayerMoveC2SPacket.Full.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Post<PlayerMoveC2SPacket.Full> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Post<PlayerMoveC2SPacket> event);

    protected abstract void onPosition
            (PacketEvent.Post<PlayerMoveC2SPacket.PositionAndOnGround> event);

    protected abstract void onRotation
            (PacketEvent.Post<PlayerMoveC2SPacket.LookAndOnGround> event);

    protected abstract void onPositionRotation
            (PacketEvent.Post<PlayerMoveC2SPacket.Full> event);
}
