package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.world.World;

final class ListenerEntityRelativeMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<EntityS2CPacket.MoveRelative>>
{

    public ListenerEntityRelativeMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, EntityS2CPacket.MoveRelative.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityS2CPacket.MoveRelative> event) {
        PlayerEntity player = mc.player;
        World world = mc.world;
        if (player != null && world != null && player.getVehicle() != null && module.noForceBoatMove.getValue())
        {
            if (event.getPacket().getEntity(world) == player.getVehicle())
            {
                event.setCancelled(true);
            }
        }
    }

}