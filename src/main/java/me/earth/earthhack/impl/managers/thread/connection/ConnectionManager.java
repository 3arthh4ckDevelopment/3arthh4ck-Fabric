package me.earth.earthhack.impl.managers.thread.connection;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.util.Objects;
import java.util.UUID;

import static net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.ADD_PLAYER;

/**
 * This Manager posts {@link ConnectionEvent}s.
 */
public class ConnectionManager extends SubscriberImpl implements Globals
{
    public ConnectionManager()
    {
        this.listeners.add(
                new EventListener<PacketEvent.Receive<PlayerListS2CPacket>>(
                        PacketEvent.Receive.class, PlayerListS2CPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Receive<PlayerListS2CPacket> event)
                    {
                        onEvent(event);
                    }
                });
    }

    private void onEvent(PacketEvent.Receive<PlayerListS2CPacket> event)
    {
        PlayerListS2CPacket packet = event.getPacket();
        if (mc.world == null
                || packet.getActions().contains(ADD_PLAYER)
                /* && packet.getActions().contains(PlayerListS2CPacket.Action.REMOVE_PLAYER) */)
        {
            return;
        }

        packet.getEntries()
                .stream()
                .filter(Objects::nonNull)
                .filter(data ->
                        data.profile().getName() != null
                                && !data.profile().getName().isEmpty()
                                || data.profile().getId() != null)
                .forEach(data ->
                {
                    // TODO!!!:

                    // switch (packet.getActions())
                    // {
                    //     case ADD_PLAYER -> onAdd(data);
                    //     /case REMOVE_PLAYER -> onRemove(data);
                    //     default -> { /* Do nothing. */ }
                    // }
                });
    }

    private void onAdd(PlayerListS2CPacket.Entry data)
    {
        if (Bus.EVENT_BUS.hasSubscribers(ConnectionEvent.Join.class))
        {
            UUID uuid = data.profile().getId();
            String packetName = data.profile().getName();
            PlayerEntity player = mc.world.getPlayerByUuid(uuid);

            if (packetName == null && player == null)
            {
                Managers.LOOK_UP.doLookUp(
                        new LookUp(LookUp.Type.NAME, uuid)
                        {
                            @Override
                            public void onSuccess()
                            {
                                Bus.EVENT_BUS.post(new ConnectionEvent
                                        .Join(name, uuid, null));
                            }

                            @Override
                            public void onFailure()
                            {
                                /* Don't post an event. */
                            }
                        });

                return;
            }

            Bus.EVENT_BUS.post(
                    new ConnectionEvent.Join(packetName, uuid, player));
        }
    }

    private void onRemove(PlayerListS2CPacket.Entry data) // AddPlayerData
    {
        if (Bus.EVENT_BUS.hasSubscribers(ConnectionEvent.Leave.class))
        {
            UUID uuid = data.profile().getId();
            String packetName = data.profile().getName();
            PlayerEntity player = mc.world.getPlayerByUuid(uuid);

            if (packetName == null && player == null)
            {
                Managers.LOOK_UP.doLookUp(
                        new LookUp(LookUp.Type.NAME, uuid)
                        {
                            @Override
                            public void onSuccess()
                            {
                                Bus.EVENT_BUS.post(new ConnectionEvent
                                        .Leave(name, uuid, null));
                            }

                            @Override
                            public void onFailure()
                            {
                                /* Don't post an event. */
                            }
                        });

                return;
            }

            Bus.EVENT_BUS.post(
                    new ConnectionEvent.Leave(packetName, uuid, player));
        }
    }

}