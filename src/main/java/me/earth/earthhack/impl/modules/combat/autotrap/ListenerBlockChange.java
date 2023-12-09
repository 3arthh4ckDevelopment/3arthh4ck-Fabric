package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;

final class ListenerBlockChange extends ModuleListener<AutoTrap, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(AutoTrap module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        PlayerEntity player = mc.player;
        module.blackList.remove(event.getPacket().getBlockPosition());
        if (player != null
            && module.instant.getValue()
            && event.getPacket().getBlockState().getBlock() == Blocks.AIR
            && player.squaredDistanceTo(event.getPacket().getBlockPosition()) < MathUtil.square(module.range.getValue())
            && module.instantRotationCheck(event.getPacket().getBlockPosition())) {
            module.runInstantTick(event);
        }
    }

}
