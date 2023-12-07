package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public class ListenerSpawn
        extends ModuleListener<Announcer, PacketEvent.Receive<EntitySpawnS2CPacket>>
{

    public ListenerSpawn(Announcer module)
    {
        super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        //TODO: find out what 60 and 91 are
        /*
        if ((event.getPacket().getType() == 60
                || event.getPacket().getType() == 91)
                && (Math.abs(event.getPacket().getVelocityX() / 8000) > 0.001
                    || Math.abs(event.getPacket().getVelocityY() / 8000) > 0.001
                    || Math.abs(event.getPacket().getVelocityZ() / 8000) > 0.001)
                && module.miss.getValue())
        {
            Managers.ENTITIES.getPlayers()
                             .stream()
                             .filter(
                                 player -> player != mc.player && !Managers.FRIENDS.contains(
                                     player)).min(
                        Comparator.comparing(
                            player -> player.getDistanceSq(event.getPacket().getX(),
                                                           event.getPacket().getY(),
                                                           event.getPacket().getZ()))).ifPresent(
                        closestPlayer -> module.arrowMap.put(
                            event.getPacket().getId(), closestPlayer));

        }
         */
    }

}
