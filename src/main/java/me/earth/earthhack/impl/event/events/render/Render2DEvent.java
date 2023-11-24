package me.earth.earthhack.impl.event.events.render;

import net.minecraft.client.gui.DrawContext;

public class Render2DEvent {
    private final DrawContext context;
    private final float tickDelta;

    public Render2DEvent(DrawContext context, float tickDelta) {
        this.context = context;
        this.tickDelta = tickDelta;
    }

    public DrawContext getContext() {
        return context;
    }

    public float getTickDelta() {
        return tickDelta;
    }
}
