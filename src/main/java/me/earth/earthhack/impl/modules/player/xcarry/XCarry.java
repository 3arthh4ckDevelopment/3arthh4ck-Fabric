package me.earth.earthhack.impl.modules.player.xcarry;

import com.google.common.eventbus.Subscribe;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module
{
    public XCarry()
    {
        super("XCarry", Category.Player);
        this.listeners.add(new ListenerCloseWindow(this));
        this.setData(new SimpleData(this, "Allows you to store items in " +
                "your crafting inventory and drag slot."));
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket packet && packet.getSyncId() == mc.player.playerScreenHandler.syncId && checkSlots()) {
            event.setCancelled(true);
        }
    }

    public boolean checkSlots() {
        for (int i = 1; i <= 4; ++i) {
            if (!mc.player.currentScreenHandler.getSlot(i).getStack().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
