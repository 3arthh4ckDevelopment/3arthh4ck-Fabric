package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;

final class ListenerBlockChange extends ModuleListener<AntiSurround,
        PacketEvent.Post<BlockUpdateS2CPacket>>
{
    public ListenerBlockChange(AntiSurround module)
    {
        super(module, PacketEvent.Post.class, BlockUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<BlockUpdateS2CPacket> event)
    {
        if (!module.async.getValue()
            || module.active.get()
            || mc.player == null
            || module.holdingCheck())
        {
            return;
        }

        if (event.getPacket().getState().isReplaceable())
        {
            module.onBlockBreak(event.getPacket().getPos(),
                                Managers.ENTITIES.getPlayers(),
                                Managers.ENTITIES.getEntities());
        }
    }

}
