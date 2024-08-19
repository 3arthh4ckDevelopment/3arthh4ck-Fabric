package me.earth.earthhack.impl.util.thread;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;

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

    /**
     * Enchants the given stack with the enchantment represented
     * by the given enchantment, and the given level.
     *
     * @param stack the stack to enchant.
     * @param enchantment the enchantment to add.
     * @param level the level for the enchantment.
     * @throws NullPointerException if no Enchantment for the id is found.
     */
    public static void addEnchantment(ItemStack stack, RegistryKey<Enchantment> enchantment, int level)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("lvl", level);
        nbt.putString("id", enchantment.getTranslationKey().replace("enchantment.minecraft.", "minecraft:"));
        NbtList list = stack.getOrCreateNbt().getList("Enchantments", 10);
        list.add(nbt);
        stack.getOrCreateNbt().put("Enchantments", list);
    }

}
