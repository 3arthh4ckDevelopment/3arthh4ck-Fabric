package me.earth.earthhack.impl.modules.misc.skinblink;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerModelPart;

final class ListenerGameLoop extends ModuleListener<SkinBlink, GameLoopEvent>
{
    public ListenerGameLoop(SkinBlink module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (module.delay.getValue() != 0) {
            if (module.timer.passed(module.delay.getValue())) {
                for (PlayerModelPart part : PlayerModelPart.values()) {
                    mc.options
                            .togglePlayerModelPart(part,
                                    module.random.getValue()
                                            ? Math.random() < 0.5
                                            : !mc.options
                                            .enabledPlayerModelParts
                                            .contains(part));
                }

                module.timer.reset();
            }
        }
    }

}
