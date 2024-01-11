package me.earth.earthhack.impl.event.listeners;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;

public abstract class EntityS2CPacketListener extends SubscriberImpl
        implements Globals
{
    public EntityS2CPacketListener()
    {
        this(EventBus.DEFAULT_PRIORITY);
    }

    public EntityS2CPacketListener(int priority)
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<EntityS2CPacket>>
                (PacketEvent.Receive.class, priority, EntityS2CPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<EntityS2CPacket> event)
            {
                onPacket(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<EntityS2CPacket.MoveRelative>>
                (PacketEvent.Receive.class, priority, EntityS2CPacket.MoveRelative.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<EntityS2CPacket.MoveRelative> event)
            {
                onPosition(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<EntityS2CPacket.Rotate>>
                (PacketEvent.Receive.class, priority, EntityS2CPacket.Rotate.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<EntityS2CPacket.Rotate> event)
            {
                onRotation(event);
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative>>
                (PacketEvent.Receive.class,
                        priority,
                        EntityS2CPacket.RotateAndMoveRelative.class)
        {
            @Override
            public void invoke
                    (PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative> event)
            {
                onPositionRotation(event);
            }
        });
    }

    protected abstract void onPacket
            (PacketEvent.Receive<EntityS2CPacket> event);

    protected abstract void onPosition
            (PacketEvent.Receive<EntityS2CPacket.MoveRelative> event);

    protected abstract void onRotation
            (PacketEvent.Receive<EntityS2CPacket.Rotate> event);

    protected abstract void onPositionRotation
            (PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative> event);

}
