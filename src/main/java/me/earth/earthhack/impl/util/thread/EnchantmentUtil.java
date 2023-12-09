package me.earth.earthhack.impl.util.thread;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Utility for {@link Enchantment}s.
 */
public class EnchantmentUtil
{
    /**
     * The part of minecraft's EnchantmentHelper needed
     * to calculate Explosion Damage for AutoCrystal etc..
     * But implemented in a way that allows you to access
     * it from multiple threads at the same time. Still not
     * safe regarding the list of item stacks.
     *
     * @param stacks the stacks to check.
     * @param source the damage source.
     */
    public static int getEnchantmentModifierDamage(Iterable<ItemStack> stacks, DamageSource source) {
        int modifier = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.get(stack).entrySet()) {
                    modifier += entry.getKey().getProtectionAmount(entry.getValue(), source);
                }
            }
        }

        return modifier;
    }

}
