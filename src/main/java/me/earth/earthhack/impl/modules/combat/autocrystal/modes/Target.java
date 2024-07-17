package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public enum Target implements Globals
{
    Closest
    {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players,
                                      List<PlayerEntity> enemies,
                                      double maxRange)
        {
            return EntityUtil.getClosestEnemy(mc.player.getX(),
                                              mc.player.getY(),
                                              mc.player.getZ(),
                                              maxRange,
                                              enemies,
                                              players);
        }
    },
    FOV
    {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players,
                                      List<PlayerEntity> enemies,
                                      double maxRange)
        {
           PlayerEntity enemy = getByFov(enemies, maxRange);
           if (enemy == null)
           {
               return getByFov(players, maxRange);
           }

           return enemy;
        }
    },
    Angle
    {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players,
                                      List<PlayerEntity> enemies,
                                      double maxRange)
        {
            PlayerEntity enemy = getByAngle(enemies, maxRange);
            return enemy == null ? getByAngle(players, maxRange) : enemy;
        }
    }, //TODO: rewrite enemy and test, then backport to 1.12.2
    Damage
    {
        @Override
        public PlayerEntity getTarget(List<PlayerEntity> players,
                                      List<PlayerEntity> enemies,
                                      double maxRange)
        {
            return null;
        }
    };

    public abstract PlayerEntity getTarget(List<PlayerEntity> players,
                                           List<PlayerEntity> enemies,
                                           double maxRange);

    public static PlayerEntity getByFov(List<PlayerEntity> players,
                                        double maxRange)
    {
        PlayerEntity closest = null;
        double closestAngle  = 360.0;
        for (PlayerEntity player : players)
        {
            if (!EntityUtil.isValid(player, maxRange))
            {
                continue;
            }

            double angle = RotationUtil.getAngle(player, 1.4);
            if (angle < closestAngle
                    && angle < (double) mc.options.getFov().getValue() / 2)
            {
                closest = player;
                closestAngle = angle;
            }
        }

        return closest;
    }

    public static PlayerEntity getByAngle(List<PlayerEntity> players,
                                          double maxRange)
    {
        PlayerEntity closest = null;
        double closestAngle  = 360.0;
        for (PlayerEntity player : players)
        {
            if (!EntityUtil.isValid(player, maxRange))
            {
                continue;
            }

            double angle = RotationUtil.getAngle(player, 1.4);
            if (angle < closestAngle
                    && angle < (double) mc.options.getFov().getValue() / 2)
            {
                closest = player;
                closestAngle = angle;
            }
        }

        return closest;
    }

    public static final String DESCRIPTION =
            """
                    - Closest, will target the closest Enemy.
                    - FOV, will target the player you are looking at (by Angle).
                    - Angle, similar to FOV but will also target players outside your FOV.
                    - Damage, Calculates Damages for all Players in Range and takes the best one (intensive).""";

}
