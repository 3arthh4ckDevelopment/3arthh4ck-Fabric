package me.earth.earthhack.impl.event.events.render;

import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent
{
    private final float delta;
    private final MatrixStack stack;

    public Render3DEvent(MatrixStack stack, float deltaIn)
    {
        this.stack = stack;
        this.delta = deltaIn;
    }

    public float getDelta()
    {
        return delta;
    }

    public MatrixStack getStack(){
        return stack;
    }

}
