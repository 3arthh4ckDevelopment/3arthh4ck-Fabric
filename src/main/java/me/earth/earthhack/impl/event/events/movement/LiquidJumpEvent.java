package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.LivingEntity;

public class LiquidJumpEvent extends Event
{
    private final LivingEntity entity;

    public LiquidJumpEvent(LivingEntity entity)
    {
        this.entity = entity;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

}
