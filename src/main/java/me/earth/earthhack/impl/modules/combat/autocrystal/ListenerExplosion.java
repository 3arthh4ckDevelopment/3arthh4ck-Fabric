package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

/**
 * Creates a new Thread when an Explosion packet is received.
 * See AutoCrystal - MultiThread - ExplosionThread.
 */
final class ListenerExplosion extends
        ModuleListener<AutoCrystal, PacketEvent.Receive< ExplosionS2CPacket>>
{
    public ListenerExplosion(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        if (module.explosionThread.getValue()
                && !event.getPacket().getAffectedBlocks().isEmpty())
        {
            module.threadHelper.schedulePacket(event);
        }
    }

}
