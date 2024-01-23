package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;

final class ListenerMotion extends ModuleListener<Spectate, MotionUpdateEvent>
{
    public ListenerMotion(Spectate module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE && module.rotate.getValue())
        {
            Entity targetedEntity = mc.targetedEntity;
            if (targetedEntity != null)
            {
                float[] rotations = RotationUtil.getRotations(targetedEntity.getX(),
                                                              targetedEntity.getY(),
                                                              targetedEntity.getZ(),
                                                              mc.player);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            }
        }
    }

}
