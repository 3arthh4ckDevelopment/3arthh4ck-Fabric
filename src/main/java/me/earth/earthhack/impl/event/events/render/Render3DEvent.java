package me.earth.earthhack.impl.event.events.render;

import net.minecraft.client.util.math.MatrixStack;

public class Render3DEvent
{
    private final MatrixStack matrices;
    private final float delta;

    public Render3DEvent(MatrixStack matrices, float delta)
    {
        this.matrices = matrices;
        this.delta = delta;
    }

    public MatrixStack getMatrix() {
        return matrices;
    }

    public float getPartialTicks()
    {
        return delta;
    }

}
