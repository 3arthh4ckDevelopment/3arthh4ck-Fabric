package me.earth.earthhack.impl.util.math;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class VectorUtil {
    @Nullable
    public static Vec3d getIntermediateWithXValue(Vec3d vec, Vec3d vec2, double x) {
        double d0 = vec.x - vec2.x;
        double d1 = vec.y - vec2.y;
        double d2 = vec.z - vec2.z;
        if (d0 * d0 < 1.0000000116860974E-7) {
            return null;
        } else {
            double d3 = (x - vec2.x) / d0;
            return d3 >= 0.0 && d3 <= 1.0 ? new Vec3d(vec2.x + d0 * d3, vec2.y + d1 * d3, vec2.z + d2 * d3) : null;
        }
    }

    @Nullable
    public static Vec3d getIntermediateWithYValue(Vec3d vec, Vec3d vec2, double y) {
        double d0 = vec.x - vec2.x;
        double d1 = vec.y - vec2.y;
        double d2 = vec.z - vec2.z;
        if (d1 * d1 < 1.0000000116860974E-7) {
            return null;
        } else {
            double d3 = (y - vec2.y) / d1;
            return d3 >= 0.0 && d3 <= 1.0 ? new Vec3d(vec2.x + d0 * d3, vec2.y + d1 * d3, vec2.z + d2 * d3) : null;
        }
    }

    @Nullable
    public static Vec3d getIntermediateWithZValue(Vec3d vec, Vec3d vec2, double z) {
        double d0 = vec.x - vec2.x;
        double d1 = vec.y - vec2.y;
        double d2 = vec.z - vec2.z;
        if (d2 * d2 < 1.0000000116860974E-7) {
            return null;
        } else {
            double d3 = (z - vec2.z) / d2;
            return d3 >= 0.0 && d3 <= 1.0 ? new Vec3d(vec2.x + d0 * d3, vec2.y + d1 * d3, vec2.z + d2 * d3) : null;
        }
    }
}
