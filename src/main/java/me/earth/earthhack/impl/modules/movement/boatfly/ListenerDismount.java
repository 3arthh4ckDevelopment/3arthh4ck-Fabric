package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;

final class ListenerDismount extends
        ModuleListener<BoatFly, PacketEvent.Receive<EntityPassengersSetS2CPacket>>
{
    public ListenerDismount(BoatFly module)
    {
        super(module, PacketEvent.Receive.class, EntityPassengersSetS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityPassengersSetS2CPacket> event)
    {
        PlayerEntity player = mc.player;
        if (player == null)
        {
            return;
        }

        //TODO: Check if this is correct
        Entity riding = mc.player.getVehicle();
        if (riding != null
                && event.getPacket().getPassengerIds()[0] == riding.getId()
                && module.remount.getValue())
        {
            event.setCancelled(true);
            if (module.schedule.getValue())
            {
                mc.execute(() ->
                        remove(event.getPacket(), player, riding));
            }
            else
            {
                remove(event.getPacket(), player, riding);
            }
        }
    }

    private void remove(EntityPassengersSetS2CPacket packet,
                        Entity player,
                        Entity riding)
    {
        for (int id : packet.getPassengerIds())
        {
            if (id == player.getId())
            {
                if (module.remountPackets.getValue())
                {
                    module.sendPackets(riding);
                }
            }
            else
            {
                try
                {
                    Entity entity = mc.world.getEntityById(id);
                    if (entity != null)
                    {
                        entity.dismountVehicle();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}