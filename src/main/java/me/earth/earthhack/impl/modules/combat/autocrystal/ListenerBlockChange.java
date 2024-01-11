package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;


final class ListenerBlockChange extends ModuleListener<AutoCrystal,
        PacketEvent.Receive<BlockUpdateS2CPacket>>
{
    public ListenerBlockChange(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockUpdateS2CPacket> event)
    {
        if ((module.multiThread.getValue() || module.mainThreadThreads.getValue())
                && module.blockChangeThread.getValue())
        {
            BlockUpdateS2CPacket packet = event.getPacket();
            if (packet.getState().getBlock() == Blocks.AIR
                    && mc.player.squaredDistanceTo(packet.getPos().toCenterPos()) < 40)
            {
                event.addPostEvent(() ->
                {
                    if (mc.world != null
                        && mc.player != null
                        && HelperUtil.validChange(packet.getPos(),
                                                  Managers.ENTITIES.getPlayers()))
                    {
                        module.threadHelper.startThread();
                    }
                });
            }
        }
    }

}
