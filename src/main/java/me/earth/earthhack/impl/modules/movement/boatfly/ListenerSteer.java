package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;

final class ListenerSteer extends
        ModuleListener<BoatFly, PacketEvent.Send<BoatPaddleStateC2SPacket>>
{
    public ListenerSteer(BoatFly module)
    {
        super(module, PacketEvent.Send.class, BoatPaddleStateC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<BoatPaddleStateC2SPacket> event)
    {
        if (module.noSteer.getValue())
        {
            event.setCancelled(true); // TODO: more settings
        }
    }

}