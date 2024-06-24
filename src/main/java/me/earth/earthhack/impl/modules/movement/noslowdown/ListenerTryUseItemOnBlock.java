package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

final class ListenerTryUseItemOnBlock
        extends ModuleListener<NoSlowDown, PacketEvent.Post<PlayerInteractBlockC2SPacket>>
{

    public ListenerTryUseItemOnBlock(NoSlowDown module)
    {
        super(module, PacketEvent.Post.class, PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event)
    {
        Item item = mc.player.getMainHandStack().getItem();
        if (module.superStrict.getValue() &&
                (item.isFood()
                        || item instanceof BowItem
                        || item instanceof PotionItem))
        {
            // int slot = mc.player.getInventory().currentItem;
            // InventoryUtil.switchTo(mc.player.getInventory().currentItem + 1);
            // InventoryUtil.switchTo(slot);
            NetworkUtil.send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            // NetworkUtil.send(new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, Direction.DOWN)); // ????????
        }
    }

}
