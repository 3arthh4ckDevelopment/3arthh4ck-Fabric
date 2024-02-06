package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;

final class ListenerBlockChange extends
        ModuleListener<AutoMine, PacketEvent.Receive<BlockUpdateS2CPacket>>
{
    public ListenerBlockChange(AutoMine module)
    {
        super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<BlockUpdateS2CPacket> event)
    {
        if (!module.resetOnPacket.getValue())
        {
            return;
        }

        BlockUpdateS2CPacket packet = event.getPacket();
        mc.execute(() ->
        {
            if (module.constellation != null && module.constellation.isAffected(
                    packet.getPos(), packet.getState()))
            {
                module.constellation = null;
            }
        });
    }

}
