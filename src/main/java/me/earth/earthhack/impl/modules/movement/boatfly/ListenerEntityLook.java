package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.world.World;

final class ListenerEntityLook extends
        ModuleListener<BoatFly, PacketEvent.Receive<EntityS2CPacket.Rotate>>
{

    public ListenerEntityLook(BoatFly module) {
        super(module, PacketEvent.Receive.class, EntityS2CPacket.Rotate.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityS2CPacket.Rotate> event) {
        PlayerEntity player = mc.player;
        World world = mc.world;
        if (player != null && player.getVehicle() != null && module.noForceBoatMove.getValue())
        {
            if (event.getPacket().getEntity(world) == player.getVehicle())
            {
                event.setCancelled(true);
            }
        }
    }

}