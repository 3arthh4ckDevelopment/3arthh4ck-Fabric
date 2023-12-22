package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.input.Input;

public class MovementInputEvent extends Event
{
    private final Input input;

    public MovementInputEvent(Input input)
    {
        this.input = input;
    }

    public Input getInput()
    {
        return input;
    }

}
