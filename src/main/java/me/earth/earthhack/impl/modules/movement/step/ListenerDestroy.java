package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

final class ListenerDestroy extends
        ModuleListener<Step, PacketEvent.Post<PlayerActionC2SPacket>>
{
    public ListenerDestroy(Step module)
    {
        super(module, PacketEvent.Post.class, PlayerActionC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerActionC2SPacket> event)
    {
        if (event.getPacket().getAction() ==
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK)
        {
            module.onBreak();
        }
    }

}
