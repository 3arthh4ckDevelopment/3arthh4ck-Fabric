package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.item.BowItem;

final class ListenerMove extends ModuleListener<BowKiller, MoveEvent>
{
    public ListenerMove(BowKiller module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (!RotationUtil.getRotationPlayer().verticalCollision)
            return;
        if (module.staticS.getValue()
            && RotationUtil.getRotationPlayer().getActiveItem().getItem() instanceof BowItem && module.blockUnder)
        {
            RotationUtil.getRotationPlayer().setVelocity(0, 0, 0);
            event.setX(0);
            event.setY(0);
            event.setZ(0);
        }
    }

}
