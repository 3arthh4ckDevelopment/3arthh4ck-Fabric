package me.earth.earthhack.impl.modules.combat.autotrap;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;

final class ListenerBlockChange extends ModuleListener<AutoTrap, PacketEvent.Receive<BlockUpdateS2CPacket>> {
    public ListenerBlockChange(AutoTrap module) {
        super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockUpdateS2CPacket> event) {
        PlayerEntity player = mc.player;
        module.blackList.remove(event.getPacket().getPos());
        if (player != null
            && module.instant.getValue()
            && event.getPacket().getState().getBlock() == Blocks.AIR
            && player.squaredDistanceTo(event.getPacket().getPos().toCenterPos()) < MathUtil.square(module.range.getValue())
            && module.instantRotationCheck(event.getPacket().getPos())) {
            module.runInstantTick(event);
        }
    }

}
