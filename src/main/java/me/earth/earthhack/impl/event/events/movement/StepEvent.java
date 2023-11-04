package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.event.events.StageEvent;
import net.minecraft.util.math.Box;

public class StepEvent extends StageEvent
{
    private final Box bb;
    private float height;

    public StepEvent(Stage stage, Box bb, float height)
    {
        super(stage);
        this.height = height;
        this.bb = bb;
    }

    public float getHeight()
    {
        return height;
    }

    /**
     * Sets the step height, if the
     * event is on Stage.PRE, otherwise
     * height is effectively final.
     *
     * @param height the new height.
     */
    public void setHeight(float height)
    {
        if (this.getStage() == Stage.PRE)
        {
            this.height = height;
        }
    }

    public Box getBB()
    {
        return bb;
    }

}
