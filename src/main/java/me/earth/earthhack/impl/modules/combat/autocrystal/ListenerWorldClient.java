package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

/**
 * Resets the AutoCrystal whenever you load into a world.
 */
final class ListenerWorldClient extends
        ModuleListener<AutoCrystal, WorldClientEvent.Load>
{
    public ListenerWorldClient(AutoCrystal module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load event)
    {
        module.reset();
    }

}
