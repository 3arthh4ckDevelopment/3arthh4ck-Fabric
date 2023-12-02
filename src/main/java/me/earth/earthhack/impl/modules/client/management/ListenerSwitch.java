package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class ListenerSwitch
    extends ModuleListener<Management, PacketEvent.Send<UpdateSelectedSlotC2SPacket>> {
    public ListenerSwitch(Management module) {
        super(module,
              PacketEvent.Send.class,
              Integer.MAX_VALUE - 100,
                UpdateSelectedSlotC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<UpdateSelectedSlotC2SPacket> event) {
        CooldownBypass bypass = module.manualCooldownBypass.getValue();
        if (bypass != CooldownBypass.None) {
            bypass.switchTo(event.getPacket().getSelectedSlot());
            event.setCancelled(true);
        }
    }

}
