package me.earth.earthhack.impl.util.client;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.hud.data.DefaultHudData;

public class SimpleHudData extends DefaultHudData<HudElement>
{
    private final int color;
    private final String description;

    public SimpleHudData(HudElement element, String description)
    {
        this(element, description, 0xffffffff);
    }

    public SimpleHudData(HudElement element, String description, int color)
    {
        super(element);
        this.color = color;
        this.description = description;
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

}
