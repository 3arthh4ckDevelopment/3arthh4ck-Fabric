package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

final class ListenerPostVehicleMove extends
        ModuleListener<BoatFly, PacketEvent.Post<VehicleMoveC2SPacket>>
{
    public ListenerPostVehicleMove(BoatFly module)
    {
        super(module, PacketEvent.Post.class, VehicleMoveC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<VehicleMoveC2SPacket> event)
    {
        Entity riding = mc.player.getVehicle();
        if (riding != null
                && !module.packetSet.contains(event.getPacket())
                && module.bypass.getValue()
                && module.postBypass.getValue()
                && module.tickCount++ >= module.ticks.getValue())
        {
            for (int i = 0; i <= module.packets.getValue(); i++)
            {
                module.sendPackets(riding);
            }

            module.tickCount = 0;
        }
    }

}