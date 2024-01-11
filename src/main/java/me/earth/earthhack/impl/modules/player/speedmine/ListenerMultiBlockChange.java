package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerMultiBlockChange extends
        ModuleListener<Speedmine, PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerMultiBlockChange(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        ExplosionS2CPacket packet = event.getPacket();
        if (module.mode.getValue() == MineMode.Fast) {
            for (BlockPos data
                    : packet.getAffectedBlocks()) {
                module.fastHelper.onBlockChange(data,
                        mc.world.getBlockState(data));
            }
            return;
        }

        if ((module.mode.getValue() != MineMode.Smart || module.sentPacket)
                && module.mode.getValue() != MineMode.Instant
                && module.mode.getValue() != MineMode.Civ)
        {
            for (BlockPos data :
                    packet.getAffectedBlocks())
            {
                if (data.equals(module.pos)
                        && mc.world.getBlockState(data).getBlock() == Blocks.AIR)
                {
                    mc.execute(module::reset);
                }
            }
        }
    }

}
