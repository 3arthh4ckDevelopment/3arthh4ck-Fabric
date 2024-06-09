package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

public class UpdateCameraEvent extends Event
{
    // Imma leave this here in case this event is used later
    private BlockView area;
    private Entity focusedEntity;
    private boolean thirdPerson;
    private boolean inverseView;
    private float tickDelta;

    public UpdateCameraEvent(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta)
    {
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.inverseView = inverseView;
        this.tickDelta = tickDelta;
    }
}
