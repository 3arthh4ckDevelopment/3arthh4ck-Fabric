package me.earth.earthhack.impl.core.ducks.util;

/**
 * Duck interface for {@link net.minecraft.text.ClickEvent}.
 */
public interface IClickEvent
{

    void setRunnable(Runnable runnable);

    Runnable getRunnable();

}
