package me.earth.earthhack.impl.modules.player.phobotspeedmine;

import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerDamageBlock extends ModuleListener<PhobotSpeedmine, DamageBlockEvent>
{
    public ListenerDamageBlock(PhobotSpeedmine module) {
        super(module, DamageBlockEvent.class);
    }

    @Override
    public void invoke(DamageBlockEvent event) {
        if (mc.player.isCreative()) {
            module.reset();
        }
    }
}
