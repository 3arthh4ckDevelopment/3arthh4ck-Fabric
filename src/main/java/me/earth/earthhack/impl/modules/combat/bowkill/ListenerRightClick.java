package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.item.Items;

final class ListenerRightClick extends ModuleListener<BowKiller, RightClickItemEvent>
{

    public ListenerRightClick(BowKiller module)
    {
        super(module, RightClickItemEvent.class);
    }

    @Override
    public void invoke(RightClickItemEvent event)
    {
        if (!RotationUtil.getRotationPlayer().verticalCollision)
            return;
        if (RotationUtil.getRotationPlayer().isHolding(Items.BOW) && module.blockUnder)
        {
            module.cancelling = true;
            module.needsMessage = true;
        }
    }

}
