package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.Direction;

final class ListenerBlockChange extends
        ModuleListener<Speedmine, PacketEvent.Receive<BlockUpdateS2CPacket>>
{
    public ListenerBlockChange(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockUpdateS2CPacket> event)
    {
        BlockUpdateS2CPacket packet = event.getPacket();
        if (module.mode.getValue() == MineMode.Fast) {
            module.fastHelper.onBlockChange(packet.getPos(),
                                            packet.getState());
            return;
        }

        if (packet.getPos().equals(module.pos)
            && packet.getState().getBlock() == Blocks.AIR
            && (module.mode.getValue() != MineMode.Smart
            || module.sentPacket)
            && module.mode.getValue() != MineMode.Instant
            && module.mode.getValue() != MineMode.Civ)
        {
            mc.execute(module::reset);
        }
        else if (packet.getPos().equals(module.pos)
            && packet.getState() == mc.world.getBlockState(module.pos)
            && module.shouldAbort
            && module.mode.getValue() == MineMode.Instant)
        {
            mc.player.networkHandler.sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket
                                             .Action
                                             .START_DESTROY_BLOCK,
                                         module.pos,
                                         Direction.DOWN));
            module.shouldAbort = false;
        }
    }

}
