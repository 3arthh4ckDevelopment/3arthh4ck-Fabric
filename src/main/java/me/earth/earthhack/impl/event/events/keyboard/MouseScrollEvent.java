package me.earth.earthhack.impl.event.events.keyboard;

import me.earth.earthhack.api.event.events.Event;


public class MouseScrollEvent extends Event {
    private final double mouseX;
    private final double mouseY;
    private final double verticalAmount;

    public MouseScrollEvent(double mouseX, double mouseY, double verticalAmount) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.verticalAmount = verticalAmount;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getVerticalAmount() {
        return verticalAmount;
    }

}
