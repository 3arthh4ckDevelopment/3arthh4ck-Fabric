package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.LivingEntity;

public class DeathEvent
{
    private final LivingEntity entity;

    public DeathEvent(LivingEntity entity)
    {
        this.entity = entity;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

}
