package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<LongJump, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(LongJump module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.noKick.getValue())
        {
            mc.execute(module::disable);
        }

        module.speed       = 0.0;
        module.stage       = 0;
        module.airTicks    = 0;
        module.groundTicks = 0;
    }

}
