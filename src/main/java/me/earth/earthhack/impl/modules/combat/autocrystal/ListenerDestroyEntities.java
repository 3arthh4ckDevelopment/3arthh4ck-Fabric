package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

final class ListenerDestroyEntities extends
        ModuleListener<AutoCrystal, PacketEvent.Receive<EntitiesDestroyS2CPacket>>
{
    public ListenerDestroyEntities(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                EntitiesDestroyS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitiesDestroyS2CPacket> event)
    {
        if (module.destroyThread.getValue())
        {
            module.threadHelper.schedulePacket(event);
        }
    }

}
