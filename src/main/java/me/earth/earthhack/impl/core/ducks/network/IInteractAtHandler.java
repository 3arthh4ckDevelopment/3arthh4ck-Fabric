package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public interface IInteractAtHandler {
    void setVec(Vec3d vec3d);

    void setHand(Hand hand);
}
