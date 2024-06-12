package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerInput extends ModuleListener<Phase, MovementInputEvent>
{
    public ListenerInput(Phase module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        if (module.autoSneak.getValue())
        {
            event.getInput().sneaking = !module.requireForward.getValue()
                    || mc.options.forwardKey.isPressed();
        }
    }

}