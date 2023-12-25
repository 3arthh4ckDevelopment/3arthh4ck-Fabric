package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;

final class ListenerBlockChange extends
        ModuleListener<Surround, PacketEvent.Receive<BlockUpdateS2CPacket>>
{
    public ListenerBlockChange(Surround module)
    {
        super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockUpdateS2CPacket> event)
    {
        BlockUpdateS2CPacket packet = event.getPacket();
        event.addPostEvent(() ->
        {
            if (module.targets.contains(packet.getPos()))
            {
                if (packet.getState().getBlock() == Blocks.AIR)
                {
                    module.confirmed.remove(packet.getPos());
                    if (module.shouldInstant(false))
                    {
                        ListenerMotion.start(module);
                    }
                }
                else if (!packet.getState().isReplaceable())
                {
                    module.confirmed.add(packet.getPos());
                }
            }
        });
    }

}
