package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.text.HoverEvent;

public interface IHoverEvent
{
    HoverEvent setOffset(boolean offset);

    boolean hasOffset();

}