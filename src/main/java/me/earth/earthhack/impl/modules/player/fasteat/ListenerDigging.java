package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class ListenerDigging extends
        ModuleListener<FastEat, PacketEvent.Send<PlayerActionC2SPacket>>
{
    public ListenerDigging(FastEat module)
    {
        super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerActionC2SPacket> event)
    {
        if (module.cancel.getValue()
                && mc.player.getActiveItem().getItem().getComponents().contains(DataComponentTypes.FOOD))
        {
            PlayerActionC2SPacket packet = event.getPacket();
            if (packet.getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM
                && packet.getDirection() == Direction.DOWN
                && packet.getPos().equals(BlockPos.ORIGIN))
            {
                event.setCancelled(true);
            }
        }
    }

}
