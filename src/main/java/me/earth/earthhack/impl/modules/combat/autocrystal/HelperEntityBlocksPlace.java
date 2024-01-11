package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.ducks.entity.IPlayerEntity;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public class HelperEntityBlocksPlace {
    private final AutoCrystal module;

    public HelperEntityBlocksPlace(AutoCrystal module) {
        this.module = module;
    }

    public boolean blocksBlock(Box bb, Entity entity) {
        if (entity instanceof IPlayerEntity
            && module.blockExtrapol.getValue() != 0) {
            MotionTracker tracker =
                ((IPlayerEntity) entity).getBlockMotionTracker();
            if (tracker != null && tracker.active) {
                return switch (module.blockExtraMode.getValue()) {
                    case Extrapolated -> tracker.getBoundingBox().intersects(bb);
                    case Pessimistic -> tracker.getBoundingBox().intersects(bb)
                            || entity.getBoundingBox().intersects(bb);
                    default -> tracker.getBoundingBox().intersects(bb)
                            && entity.getBoundingBox().intersects(bb);
                };
            }
        }

        return entity.getBoundingBox().intersects(bb);
    }

}
