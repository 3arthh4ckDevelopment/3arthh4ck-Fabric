package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

// no multiblockchange on 1.20!
final class ListenerBlockMulti extends ModuleListener<AutoCrystal,
        PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerBlockMulti(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                ExplosionS2CPacket.class); // probably not necessary
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        if ((module.multiThread.getValue() || module.mainThreadThreads.getValue())
                && module.blockChangeThread.getValue())
        {
            ExplosionS2CPacket packet = event.getPacket();
            event.addPostEvent(() ->
            {
                if (mc.world == null || mc.player == null)
                {
                    return;
                }

                for (BlockPos data :
                        packet.getAffectedBlocks())
                {
                    if (mc.world.getBlockState(data).isAir()
                            && HelperUtil.validChange(data,
                                                      Managers.ENTITIES.getPlayers()))
                    {
                        module.threadHelper.startThread();
                        break;
                    }
                }
            });
        }
    }

}
