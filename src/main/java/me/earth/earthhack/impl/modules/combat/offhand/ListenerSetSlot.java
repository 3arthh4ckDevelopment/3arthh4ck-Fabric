package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

final class ListenerSetSlot extends
        ModuleListener<Offhand, PacketEvent.Receive<UpdateSelectedSlotS2CPacket>>
{
    public ListenerSetSlot(Offhand module)
    {
        super(module, PacketEvent.Receive.class, UpdateSelectedSlotS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<UpdateSelectedSlotS2CPacket> event)
    {
        module.setSlotTimer.reset();
        if (!module.async.getValue()
            || module.asyncTimer.passed(module.asyncCheck.getValue())
            || module.asyncSlot == -1
            || event.getPacket().getSlot() != module.asyncSlot)
        {
            return;
        }

        event.setCancelled(true);
        // if (PingBypass.isConnected()
        //     && module.fixPingBypassAsyncSlot.getValue()) {
        //     event.setPingBypassCancelled(true);
        // }

        module.asyncSlot = -1;
    }

}
