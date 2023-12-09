package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;


public class HelperUtil implements Globals
{

    //TODO: redo when CA
    public static boolean valid(Entity entity, double range, double trace)
    {
        PlayerEntity player = RotationUtil.getRotationPlayer();
        double d = entity.squaredDistanceTo(player);
        if (d >= MathUtil.square(range))
        {
            return false;
        }

        if (d >= trace)
        {
            return RayTraceUtil.canBeSeen(entity, player);
        }

        return true;
    }

}
