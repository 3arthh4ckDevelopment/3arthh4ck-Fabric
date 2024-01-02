package me.earth.earthhack.impl.util.otherplayers;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.player.PlayerEntity;

public class IgnoreSelfClosest implements Globals {
    public static PlayerEntity GetClosestIgnore(Double maxdist) {
        double closestDistance = Double.MAX_VALUE;
        double calcDistance = Double.MAX_VALUE;
        PlayerEntity closestPlayer = null;
        for (PlayerEntity player : Managers.ENTITIES.getPlayers()) {
            if (player.squaredDistanceTo(mc.player) < closestDistance) {
                if (player != mc.player) {
                    closestPlayer = player;
                    closestDistance = player.squaredDistanceTo(mc.player);
                    calcDistance = player.distanceTo(mc.player);
                }
            }
        }

        if (calcDistance <= maxdist) {
            return closestPlayer;
        } else {
            return null;
        }
    }
}
