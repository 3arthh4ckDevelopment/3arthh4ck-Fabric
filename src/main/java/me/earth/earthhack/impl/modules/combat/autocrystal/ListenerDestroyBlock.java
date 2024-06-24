package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.player.PlayerEntity;

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
            && HelperUtil.validChange(event.getPos(), CollectionUtil.convertElements(mc.world.getPlayers(), PlayerEntity.class)))
        {
            module.threadHelper.startThread(event.getPos().down());
        }
    }

}
