package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;

final class ListenerEntityTeleport extends
        ModuleListener<BoatFly, PacketEvent.Receive<EntityPositionS2CPacket>>
{

    public ListenerEntityTeleport(BoatFly module) {
        super(module, PacketEvent.Receive.class, EntityPositionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityPositionS2CPacket> event) {
        PlayerEntity player = mc.player;
        if (player != null && player.getVehicle() != null && module.noForceBoatMove.getValue())
        {
            if (event.getPacket().getEntityId() == player.getVehicle().getId())
            {
                event.setCancelled(true);
            }
        }
    }

}