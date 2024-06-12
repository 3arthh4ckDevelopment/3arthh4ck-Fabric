package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;

final class ListenerServerVehicleMove extends
        ModuleListener<BoatFly, PacketEvent.Receive<VehicleMoveS2CPacket>>
{
    public ListenerServerVehicleMove(BoatFly module)
    {
        super(module, PacketEvent.Receive.class, VehicleMoveS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<VehicleMoveS2CPacket> event)
    {
        if (module.noVehicleMove.getValue())
        {
            event.setCancelled(true);
        }
    }

}