package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class ListenerTryUseItem extends
        ModuleListener<FastEat, PacketEvent.Send<PlayerInteractItemC2SPacket>>
{
    public ListenerTryUseItem(FastEat module)
    {
        super(module, PacketEvent.Send.class, PlayerInteractItemC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractItemC2SPacket> event)
    {
        if (module.mode.getValue() == FastEatMode.Update
                && module.isValid(mc.player.getStackInHand(event.getPacket()
                                                             .getHand())))
        {
            // no need to authorize it's NoEvent
            NetworkUtil.sendPacketNoEvent(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    Direction.DOWN));
        }
    }

}
