package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

public class MutPos extends BlockPos.Mutable {
    public void incrementX(int by) {
        this.setX(this.getX() + by);
    }

    public void incrementY(int by) {
        this.setY(this.getY() + by);
    }

    public void incrementZ(int by) {
        this.setZ(this.getZ() + by);
    }
}
