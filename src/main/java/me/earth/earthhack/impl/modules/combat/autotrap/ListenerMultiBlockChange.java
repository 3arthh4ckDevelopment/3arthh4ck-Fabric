package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;

final class ListenerMultiBlockChange extends ModuleListener<AutoTrap, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(AutoTrap module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        for (SPacketMultiBlockChange.BlockUpdateData data : event.getPacket().getChangedBlocks())
        {
            module.blackList.remove(data.getPos());
        }

        PlayerEntity player = mc.player;
        if (player != null
            && module.instant.getValue()
            && Arrays.stream(event.getPacket().getChangedBlocks())
                     .filter(d -> d.getBlockState().getBlock() == Blocks.AIR)
                     .map(SPacketMultiBlockChange.BlockUpdateData::getPos)
                     .filter(module::instantRotationCheck)
                     .anyMatch(pos -> player.squaredDistanceTo(pos) < MathUtil.square(module.range.getValue()))) {
            module.runInstantTick(event);
        }
    }

}
