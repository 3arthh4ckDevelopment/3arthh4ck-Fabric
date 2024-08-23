package me.earth.earthhack.impl.util.thread;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Utility for {@link Enchantment}s.
 */
public class EnchantmentUtil implements Globals
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
        MinecraftClient mc = MinecraftClient.getInstance();
        int modifier = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                modifier += EnchantmentHelper.getProtectionAmount(mc.getServer().getOverworld(), mc.player, source);
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
        RegistryEntry<Enchantment> entry = convertEnchantmentKeyToEntry(enchantment);
        if (entry != null) {
            stack.addEnchantment(entry, level);
        }
        Earthhack.getLogger().error("Failed to apply enchantment: " + enchantment.getValue().toString());
    }

    /**
     * Converts {@link RegistryKey<Enchantment>} to a {@link RegistryEntry<Enchantment>}.
     */
    public static RegistryEntry<Enchantment> convertEnchantmentKeyToEntry(RegistryKey<Enchantment> key) {
        if (mc.world == null) return null;
        return mc.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(key).orElse(null);
    }

    /**
     * Thanks cattyn again
     * <a href="https://github.com/mioclient/oyvey-ported/blob/master/src/main/java/me/alpha432/oyvey/util/EnchantmentUtil.java">...</a>
     */
    public static int getLevel(RegistryKey<Enchantment> key, ItemStack stack) {
        if (stack.isEmpty()) return 0;
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> enchantment : stack.getEnchantments().getEnchantmentEntries()) {
            if (enchantment.getKey().matchesKey(key)) return enchantment.getIntValue();
        }
        return 0;
    }

    public static boolean has(RegistryKey<Enchantment> key, EquipmentSlot slot, LivingEntity entity) {
        return getLevel(key, entity.getEquippedStack(slot)) > 0;
    }

}
