package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.module.data.DefaultData;

final class AntiAimData extends DefaultData<AntiAim>
{
    public AntiAimData(AntiAim module)
    {
        super(module);
        register(module.strict, "Doesn't rotate when you place/attack.");
        register(module.skip, "Skips every Nth tick, off if value is 1.");
        register(module.flipYaw, "If you want to flip your yaw.");
        register(module.yawSlices, "How many slices of yaw you want to use on mode ViewLock.");
        register(module.pitchSlices, "How many slices of pitch you want to use on mode ViewLock.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "";
    }

}

