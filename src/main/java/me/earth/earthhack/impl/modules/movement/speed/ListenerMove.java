package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;

public class ListenerMove extends ModuleListener<Speed, MoveEvent> {

    // private static final ModuleCache<PacketFly> PACKET_FLY =
    //         Caches.getModule(PacketFly.class);
    // private static final ModuleCache<Freecam> FREECAM =
    //         Caches.getModule(Freecam.class);
    // private static final ModuleCache<NCPTweaks> NCP_TWEAKS =
    //         Caches.getModule(NCPTweaks.class);

    public ListenerMove(Speed module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
       // if (PACKET_FLY.isEnabled()
       //         || FREECAM.isEnabled()
       //         || NCP_TWEAKS.isEnabled() && NCP_TWEAKS.get().isSpeedStopped())
       // {
       //     return;
       // }

        if (!module.inWater.getValue()
                && (PositionUtil.inLiquid() || PositionUtil.inLiquid(true))
                || mc.player.isHoldingOntoLadder()
                || mc.player.getBlockStateAtPos().isOpaque())
        {
            module.stop = true;
            return;
        }

        if (module.stop)
        {
            module.stop = false;
            return;
        }

        module.mode.getValue().move(event, module);
        if (module.modify.getValue()) {
            event.setX(event.getX() * module.xzFactor.getValue());
            event.setY(event.getY() * module.yFactor.getValue());
            event.setZ(event.getZ() * module.xzFactor.getValue());
        }
    }
}
