package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.DistanceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerBreakingProgress extends ModuleListener<AntiTrap, PacketEvent.Receive<BlockBreakingProgressS2CPacket>> {

    public ListenerBreakingProgress(AntiTrap module) {
        super(module, PacketEvent.Receive.class, BlockBreakingProgressS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockBreakingProgressS2CPacket> event) {
        BlockBreakingProgressS2CPacket packet = event.getPacket();
        BlockPos pos = packet.getPos();

        if (!(Math.sqrt(DistanceUtil.distanceSq(pos.getX(), pos.getY(), pos.getZ(), RotationUtil.getRotationPlayer()))
                    <= module.mineRange.getValue())
                || module.hit.contains(pos)
                || Managers.FRIENDS.containsEntity(mc.world.getEntityById(packet.getEntityId()))
                || !module.waitForMine.getValue())
        {
            return;
        }

        module.hit.add(pos);
    }
}
