package me.earth.earthhack.impl.modules.player.xcarry;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

final class ListenerCloseWindow extends
        ModuleListener<XCarry, PacketEvent.Send<CloseHandledScreenC2SPacket>>
{
    public ListenerCloseWindow(XCarry module)
    {
        super(module, PacketEvent.Send.class, CloseHandledScreenC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CloseHandledScreenC2SPacket> event)
    {
        CloseHandledScreenC2SPacket packet = event.getPacket();
        if (packet.getSyncId() == mc.player.playerScreenHandler.syncId)
        {
            event.setCancelled(true);
        }
    }

}
