package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerDestroyBlock extends
        ModuleListener<AutoCrystal, BlockDestroyEvent>
{
    public ListenerDestroyBlock(AutoCrystal module)
    {
        super(module, BlockDestroyEvent.class, -10);
    }

    @Override
    public void invoke(BlockDestroyEvent event)
    {
        if (module.blockDestroyThread.getValue()
            && event.getStage() == Stage.PRE
            && module.multiThread.getValue()
            && !event.isCancelled()
            && !event.isUsed()
            && HelperUtil.validChange(event.getPos(), Managers.ENTITIES.getPlayers()))
        {
            module.threadHelper.startThread(event.getPos().down());
        }
    }

}
