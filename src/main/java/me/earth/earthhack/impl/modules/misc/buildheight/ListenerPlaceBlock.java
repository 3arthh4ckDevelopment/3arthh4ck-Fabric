package me.earth.earthhack.impl.modules.misc.buildheight;

import me.earth.earthhack.impl.core.mixins.util.IBlockHitResult;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.Direction;

final class ListenerPlaceBlock extends
        ModuleListener<BuildHeight,
                PacketEvent.Send<PlayerInteractBlockC2SPacket>>
{
    public ListenerPlaceBlock(BuildHeight module)
    {
        super(module,
                PacketEvent.Send.class,
                PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractBlockC2SPacket> event)
    {
        PlayerInteractBlockC2SPacket packet = event.getPacket();
        if (packet.getBlockHitResult().getPos().getY() >= 255
                && (!module.crystals.getValue() ||
                     mc.player.getStackInHand(packet.getHand()).getItem()
                        == Items.END_CRYSTAL)
                && packet.getBlockHitResult().getSide() == Direction.UP)
        {
            ((IBlockHitResult) packet.getBlockHitResult())
                    .earthhack$setDirection(Direction.DOWN);
        }
    }

}
