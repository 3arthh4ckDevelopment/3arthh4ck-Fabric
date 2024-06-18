package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.module.data.DefaultData;

final class ReverseStepData extends DefaultData<ReverseStep>
{
    public ReverseStepData(ReverseStep module)
    {
        super(module);
        register(module.speed, "The speed at which you'll move downwards.");
        register(module.distance, "The distance at which you'll still ReverseStep.");
        register(module.voidCheck, "Checks if you have void under you. In development!!!!"); // TODO this
        register(module.strictLiquid, "Doesn't step in liquids. Useful for anticheats that" +
                " detect this.");
    }


    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Makes falling down blocks faster.";
    }

}
