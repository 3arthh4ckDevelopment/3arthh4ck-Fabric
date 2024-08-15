package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ToolTipEvent extends Event {
    private final DrawContext context;
    private final ItemStack itemStack;
    private final int x;
    private final int y;

    public ToolTipEvent(DrawContext context, ItemStack itemStack, int x, int y) {
        this.context = context;
        this.itemStack = itemStack;
        this.x = x;
        this.y = y;
    }

    public DrawContext getContext() {
        return context;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}