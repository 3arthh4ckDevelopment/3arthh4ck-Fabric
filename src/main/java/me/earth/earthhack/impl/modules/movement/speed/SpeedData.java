package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.module.data.DefaultData;

final class SpeedData extends DefaultData<Speed>
{
    public SpeedData(Speed module)
    {
        super(module);
        register(module.mode, """
                - Instant : Always move at 20.5 km/h.
                - OldGround : Old OnGroundSpeed.
                - OnGround : Move quickly on flat surfaces.
                - Vanilla : Move quickly into all directions
                 as specified by the Speed setting.""");
        register(module.inWater, "Move quickly while in water.");
        register(module.speedSet, "Speed for Mode-Vanilla.");
        register(module.sneakCheck, "For mode Instant:\n" +
                "Checks if you are sneaking, so you don't get lagged back" +
                " on stricter servers.");
    }

    @Override
    public String getDescription()
    {
        return "Movement hacks that make you go faster.";
    }

}

