package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

final class ListenerRightClick
        extends ModuleListener<Offhand, ClickBlockEvent.Right>
{
    public ListenerRightClick(Offhand module)
    {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event)
    {
        if (module.noOGC.getValue() && event.getHand() == Hand.MAIN_HAND)
        {
            Item mainHand = mc.player.getMainHandStack().getItem();
            Item offHand  = mc.player.getOffHandStack().getItem();
            if (mainHand == Items.END_CRYSTAL
                    && offHand == Items.GOLDEN_APPLE
                    && event.getHand() == Hand.MAIN_HAND)
            {
                event.setCancelled(true);
                mc.player.setCurrentHand(Hand.OFF_HAND);
                // mc.playerController.processRightClick(mc.player,
                //                                       mc.world,
                //                                       EnumHand.OFF_HAND);
            }
        }
    }

}
