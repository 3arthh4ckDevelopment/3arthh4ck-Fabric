package me.earth.earthhack.impl.modules.movement.boatfly;

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.entity.Entity;

final class ListenerGameLoop extends ModuleListener<BoatFly, GameLoopEvent>
{
    public ListenerGameLoop(BoatFly module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (mc.player == null)
        {
            return;
        }

        Entity riding = mc.player.getVehicle();
        if (riding == null)
        {
            return;
        }

        if (mc.player.equals(riding.getControllingPassenger()))
        {
            riding.getVelocity().y = mc.options.jumpKey.isPressed()
                    ?  module.upSpeed.getValue()
                    : KeyBoardUtil.isKeyDown(module.downBind)
                    ? -module.downSpeed.getValue()
                    :  module.glide.getValue();

            if (module.fixYaw.getValue())
            {
                riding.yaw = mc.player.yaw;
            }
        }
    }

}