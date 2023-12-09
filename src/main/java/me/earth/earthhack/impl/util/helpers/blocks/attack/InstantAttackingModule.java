package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.util.math.Passable;
import net.minecraft.entity.decoration.EndCrystalEntity;

public interface InstantAttackingModule extends AttackingModule
{
    @SuppressWarnings("unused")
    default void postAttack(EndCrystalEntity entity)
    {
        // Reserved for actions that can happen
        // after the crystal has been attacked.
    }

    boolean shouldAttack(EndCrystalEntity entity);

    Passable getTimer();

    int getBreakDelay();

    int getCooldown();

}
