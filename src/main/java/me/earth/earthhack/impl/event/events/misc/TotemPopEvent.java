package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.entity.player.PlayerEntity;

public class TotemPopEvent {
    private final PlayerEntity entity;

    public TotemPopEvent( PlayerEntity entity)
    {
        this.entity = entity;
    }

    public PlayerEntity getEntity()
    {
        return entity;
    }

}
