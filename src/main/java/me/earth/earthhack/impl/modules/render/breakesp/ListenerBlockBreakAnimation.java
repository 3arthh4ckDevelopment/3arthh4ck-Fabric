package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.DistanceUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;

public class ListenerBlockBreakAnimation extends ModuleListener<BreakESP, PacketEvent.Receive<BlockBreakingProgressS2CPacket>>
{
    public ListenerBlockBreakAnimation(final BreakESP module) {
        super(module, PacketEvent.Receive.class, BlockBreakingProgressS2CPacket.class);
    }

    public void invoke(final PacketEvent.Receive<BlockBreakingProgressS2CPacket> event) {

        if (event.getPacket().getProgress() != 255) {
            if (Math.sqrt(DistanceUtil.distanceSq(event.getPacket().getPos().getX(), event.getPacket().getPos().getY(), event.getPacket().getPos().getZ(), mc.player.getX(), mc.player.getY(), mc.player.getZ())) < module.radius.getValue()) {
                BreakESPBlock exists = null;
                if (module.blocks.size() != 0) {
                    for (BreakESPBlock b : module.blocks)
                        if (b.entityID == event.getPacket().getEntityId())
                            exists = b;
                }

                if (exists != null)
                {
                    if (exists.blockPos != event.getPacket().getPos()) {
                        module.blocks.remove(exists);
                        exists = null;
                    }
                    else if (event.getPacket().getProgress() == 10) {
                        module.blocks.remove(exists);
                    }
                }
                if (exists == null
                        && mc.world.getBlockState(event.getPacket().getPos()).getBlock() != Blocks.BEDROCK
                        && mc.world.getBlockState(event.getPacket().getPos()).getBlock() != Blocks.AIR
                        && mc.world.getBlockState(event.getPacket().getPos())
                                    .isFullCube(mc.world, event.getPacket().getPos()))
                {
                    module.blocks.add(new BreakESPBlock(event.getPacket().getPos(), event.getPacket().getEntityId(), System.currentTimeMillis()));
                    if (module.chatPos.getValue())
                        Managers.CHAT.sendDeleteMessage(TextColor.AQUA + event.getPacket().getPos(), String.valueOf(module.random.nextInt()), ChatIDs.MODULE);
                }
            }
        }
    }

}