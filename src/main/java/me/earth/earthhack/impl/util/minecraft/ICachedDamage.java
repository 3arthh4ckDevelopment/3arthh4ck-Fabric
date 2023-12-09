package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Caches
 * {@link LivingEntity#getArmor()},
 * {@link SharedMonsterAttributes#ARMOR_TOUGHNESS} and
 * {@link EnchantmentUtil#getEnchantmentModifierDamage(Iterable, DamageSource)}
 * in order to prevent ConcurrentModificationExceptions when accessing
 * these on a different Thread.
 */
public interface ICachedDamage
{
    /** {@link me.earth.earthhack.impl.modules.client.safety.Safety} */
    Setting<Boolean> SHOULD_CACHE = new BooleanSetting("CacheAttributes", true);

    /**
     * @return {@link EntityLivingBase#getTotalArmorValue()}
     */
    int getArmorValue();

    /**
     * @return {@link SharedMonsterAttributes#ARMOR_TOUGHNESS}
     */
    float getArmorToughness();

    /**
     * @param source the DamageSource (Should be an Explosion).
     * @return {@link EnchantmentUtil#getEnchantmentModifierDamage(
     * Iterable, DamageSource)}
     */
    int getExplosionModifier(DamageSource source);

    /**
     * @return <tt>true</tt> if this Object caches values.
     */
    default boolean shouldCache()
    {
        return SHOULD_CACHE.getValue() && this instanceof PlayerEntity;
    }

}