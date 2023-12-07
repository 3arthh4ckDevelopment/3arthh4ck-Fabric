package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.client.gui.screen.Screen;

/**
 * Fired when {@link net.minecraft.client.MinecraftClient#setScreen(Screen)}
 * is called. The Screen that's going to be closed can be
 * checked why {@link net.minecraft.client.MinecraftClient#currentScreen}.
 *
 * @param <T> the type of screen that's gonna be displayed.
 */
public class GuiScreenEvent<T extends Screen> extends Event
{
    private final T screen;

    public GuiScreenEvent(T screen)
    {
        this.screen = screen;
    }

    public T getScreen()
    {
        return screen;
    }

}
