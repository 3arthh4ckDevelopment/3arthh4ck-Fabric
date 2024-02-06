package me.earth.earthhack.impl.modules.render.fullbright;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.fullbright.mode.BrightMode;
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
            if (module.mode.getValue() == BrightMode.Potion)
            {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 255, false, false));
            }
        }
    }

}