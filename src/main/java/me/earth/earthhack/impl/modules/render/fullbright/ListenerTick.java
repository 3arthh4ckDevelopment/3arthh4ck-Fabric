package me.earth.earthhack.impl.modules.render.fullbright;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

final class ListenerTick extends ModuleListener<Fullbright, TickEvent>
{
    public ListenerTick(Fullbright module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (event.isSafe())
        {
            switch (module.mode.getValue()) {
                case Gamma -> mc.options.getGamma().setValue(1000000.0D);
                case Potion -> {
                    mc.options.getGamma().setValue(module.oldValue);
                    mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 255, false, false));
                }
            }
        }
    }

}