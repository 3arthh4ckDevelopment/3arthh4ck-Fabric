package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.misc.EatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ChorusFruitItem;

public class ListenerEat extends ModuleListener<BlockLag, EatEvent> {
    public ListenerEat(BlockLag module) {
        super(module,EatEvent.class);
    }

    @Override
    public void invoke(EatEvent e) {
        if(e.getStack().getItem() instanceof ChorusFruitItem){
            module.ateChorus = true;
        }
    }
}
