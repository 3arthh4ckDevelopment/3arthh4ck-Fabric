package me.earth.earthhack.impl.modules.client.colors;

import me.earth.earthhack.api.module.data.DefaultData;

final class ColorsData extends DefaultData<Colors>
{
    public ColorsData(Colors module)
    {
        super(module);
    }

    @Override
    public int getColor()
    {
        return 0xff34A1FF;
    }

    @Override
    public String getDescription()
    {
        return "Gui colors. This module is always on.";
    }

}
