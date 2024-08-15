package me.earth.earthhack.impl.event.events.render;

import net.minecraft.client.gui.DrawContext;

public class InventoryRenderEvent {

    private final DrawContext context;

    public InventoryRenderEvent(DrawContext context) {
        this.context = context;
    }

    public DrawContext getContext() {
        return context;
    }

    public static class InventoryClickEvent {
        private final double x;
        private final double y;

        public InventoryClickEvent(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
