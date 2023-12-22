package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;

// TODO: ??????????????????????????????????????????????????????????????????????
final class ListenerRightClickItem extends
        ModuleListener<NoSlowDown, RightClickItemEvent>
{
    public ListenerRightClickItem(NoSlowDown module)
    {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event)
    {
        Item item = mc.player.getStackInHand(event.getHand()).getItem();
        if (module.sneakPacket.getValue() &&
                (item.isFood()
                    || item instanceof BowItem
                    || item instanceof PotionItem))
        {
            if (!Managers.ACTION.isSneaking())
            {
                /*mc.player.connection.sendPacket(
                    new CPacketEntityAction(
                                mc.player,
                                CPacketEntityAction.Action.START_SNEAKING));*/
            }
        }
    }

}
