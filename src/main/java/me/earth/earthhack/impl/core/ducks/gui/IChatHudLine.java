package me.earth.earthhack.impl.core.ducks.gui;

import net.minecraft.text.Text;

public interface IChatHudLine
{
    String getTimeStamp();
    void setComponent(Text component);
}
