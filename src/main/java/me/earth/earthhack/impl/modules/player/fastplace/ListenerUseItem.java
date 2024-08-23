package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

final class ListenerUseItem extends
        ModuleListener<FastPlace, PacketEvent.Send<PlayerInteractItemC2SPacket>>
{
    private boolean sending;

    public ListenerUseItem(FastPlace module)
    {
        super(module, PacketEvent.Send.class, PlayerInteractItemC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractItemC2SPacket> event)
    {
        if (!module.doubleEat.getValue()
            || sending
            || event.isCancelled()
            || !(mc.player.getStackInHand(event.getPacket().getHand()).getComponents().contains(DataComponentTypes.FOOD)))
        {
            return;
        }

        sending = true;
        NetworkUtil.sendSequenced(seq -> new PlayerInteractItemC2SPacket(event.getPacket().getHand(), seq, event.getPacket().getYaw(), event.getPacket().getPitch()));
        sending = false;
        /*
        PingBypass.sendToActualServer(
                new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                        BlockPos.ORIGIN,
                        Direction.DOWN));
         */
    }

}
