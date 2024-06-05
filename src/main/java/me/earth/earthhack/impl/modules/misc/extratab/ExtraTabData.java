package me.earth.earthhack.impl.modules.misc.extratab;

import me.earth.earthhack.api.module.data.DefaultData;

final class ExtraTabData extends DefaultData<ExtraTab>
{
    public ExtraTabData(ExtraTab module)
    {
        super(module);
        register(module.size,
                "How many players you want to display when pressing tab.");
        register("Ping", "If you want to display the player's " +
                "latency to the server in a numeric value.");
        register("Bars", "If you want to render the vanilla Ping " +
                "indicator bars.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Extends the tab menu.";
    }

}
