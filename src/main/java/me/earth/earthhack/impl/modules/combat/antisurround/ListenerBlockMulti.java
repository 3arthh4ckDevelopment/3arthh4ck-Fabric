package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerBlockMulti extends ModuleListener<AntiSurround,
        PacketEvent.Post<ExplosionS2CPacket>>
{
    public ListenerBlockMulti(AntiSurround module)
    {
        super(module, PacketEvent.Post.class, ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<ExplosionS2CPacket> event)
    {
        if (!module.async.getValue()
            || module.active.get()
            || mc.player == null
            || module.holdingCheck())
        {
            return;
        }

        for (BlockPos pos
                : event.getPacket().getAffectedBlocks())
        {
            if (mc.world.getBlockState(pos).isReplaceable()
                    && module.onBlockBreak(pos,
                                           Managers.ENTITIES.getPlayers(),
                                           Managers.ENTITIES.getEntities()))
            {
                break;
            }
        }
    }

}
