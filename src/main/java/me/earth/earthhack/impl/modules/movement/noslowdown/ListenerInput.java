package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.movement.MovementInputEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.input.Input;

final class ListenerInput extends ModuleListener<NoSlowDown, MovementInputEvent>
{
    public ListenerInput(NoSlowDown module)
    {
        super(module, MovementInputEvent.class);
    }

    @Override
    public void invoke(MovementInputEvent event)
    {
        Input input = event.getInput();
        if (module.items.getValue()
                && module.input.getValue()
                && input == mc.player.input
                && mc.player.getActiveHand() != null
                && !mc.player.isRiding())
        {
            input.movementSideways /= 0.2F;
            input.movementForward /= 0.2F;
        }
    }

}
