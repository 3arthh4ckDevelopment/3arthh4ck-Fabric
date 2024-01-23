package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.entity.LivingEntity}.
 */
public interface ILivingEntity //TODO: implement the mixins
{
    /** @return the getArmSwingAnimationEnd field. */
    int earthhack$armSwingAnimationEnd();

    /** @return the ticksSinceLastSwing field. */
    int earthhack$getTicksSinceLastSwing();

    int earthhack$getActiveItemStackUseCount();

    void earthhack$setTicksSinceLastSwing(int ticks);

    void earthhack$setActiveItemStackUseCount(int count);

    boolean earthhack$getElytraFlag();

    void earthhack$setLowestDura(float lowest);

    float earthhack$getLowestDurability();

}
