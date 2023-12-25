package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerExplosion extends
        ModuleListener<Surround, PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerExplosion(Surround module)
    {
        super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        ExplosionS2CPacket packet = event.getPacket();
        event.addPostEvent(() ->
        {
            for (BlockPos pos : packet.getAffectedBlocks())
            {
                module.confirmed.remove(pos);
                if (module.shouldInstant(false))
                {
                    ListenerMotion.start(module);
                }
            }
        });
    }

}
