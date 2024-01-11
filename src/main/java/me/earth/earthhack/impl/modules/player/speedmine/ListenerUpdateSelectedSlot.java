package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class ListenerUpdateSelectedSlot extends ModuleListener<Speedmine, PacketEvent.Send<UpdateSelectedSlotC2SPacket>> {

    public ListenerUpdateSelectedSlot(Speedmine module) {
        super(module, PacketEvent.Send.class, UpdateSelectedSlotC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<UpdateSelectedSlotC2SPacket> event) {
        if(module.resetSwap.getValue()
                && module.getPos() != null
                && mc.world.getBlockState(module.getPos()).getBlock() != Blocks.AIR)
        {
            module.retry();
        }
    }
}
