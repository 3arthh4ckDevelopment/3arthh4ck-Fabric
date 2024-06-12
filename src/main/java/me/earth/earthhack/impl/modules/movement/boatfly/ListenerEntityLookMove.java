package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.world.World;

final class ListenerEntityLookMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative>>
{
    public ListenerEntityLookMove(BoatFly module) {
        super(module, PacketEvent.Receive.class, EntityS2CPacket.RotateAndMoveRelative.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative> event)
    {
        World world = mc.world;
        PlayerEntity player = mc.player;
        Entity ridingEntity;
        if (module.noForceBoatMove.getValue()
                && player != null
                && world != null
                && (ridingEntity = player.getVehicle()) != null)
        {
            if (ridingEntity.equals(event.getPacket().getEntity(world)))
            {
                event.setCancelled(true);
            }
        }
    }

}