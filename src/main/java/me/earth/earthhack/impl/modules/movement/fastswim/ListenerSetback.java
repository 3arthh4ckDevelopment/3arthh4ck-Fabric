package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerSetback
        extends ModuleListener<FastSwim, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{

    public ListenerSetback(FastSwim module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        module.waterSpeed = module.hWater.getValue();
        module.lavaSpeed = module.hLava.getValue();
    }

}
