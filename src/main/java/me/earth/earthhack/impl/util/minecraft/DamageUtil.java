package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.util.math.DistanceUtil;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceResult;
import me.earth.earthhack.impl.util.math.raytrace.RayTracer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.CobwebBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class DamageUtil implements Globals
{
    public static boolean isSharper(ItemStack stack, int level)
    {
        return EnchantmentHelper.getLevel(
                                        Enchantments.SHARPNESS, stack) > level;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canBreakWeakness(boolean checkStack)
    {
        if (!mc.player.getActiveStatusEffects().containsKey(StatusEffects.WEAKNESS))
        {
            return true;
        }

        int strengthAmp = 0;
        StatusEffectInstance effect = mc.player.getActiveStatusEffects().get(StatusEffects.STRENGTH);

        if (effect != null)
        {
            strengthAmp = effect.getAmplifier();
        }

        if (strengthAmp >= 1)
        {
            return true;
        }

        return checkStack && canBreakWeakness(mc.player.getMainHandStack());
    }

    /**
     * Access PotionMap safely from a separate thread.
     *
     * @return not {@link DamageUtil#canBreakWeakness(boolean)}}, caught.
     */
    public static boolean isWeaknessed()
    {
        try
        {
            return !canBreakWeakness(true);
        }
        catch (Throwable t)
        {
            return true;
        }
    }

    /**
     * Attempts to cache the lowest Armor Percentage for
     * this Entity via {@link IEntityLivingBase#setLowestDura(float)}.
     *
     * @param base the entity whose lowest Armor Percentage to cache.
     * @return <tt>true</tt> if the Entity wears no armor.
     */
    public static boolean cacheLowestDura(LivingEntity base)
    {
        IEntityLivingBase access = (IEntityLivingBase) base;
        float before = access.getLowestDurability();
        access.setLowestDura(Float.MAX_VALUE);

        try
        {
            boolean isNaked = true;
            for (ItemStack stack : base.getArmorItems())
            {
                if (!stack.isEmpty())
                {
                    isNaked = false;
                    float damage = getPercent(stack);
                    if (damage < access.getLowestDurability())
                    {
                        access.setLowestDura(damage);
                    }
                }
            }

            return isNaked;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            access.setLowestDura(before);
            return false;
        }
    }

    public static boolean canBreakWeakness(ItemStack stack)
    {
        if (stack.getItem() instanceof SwordItem)
        {
            return true;
        }

        if (stack.getItem() instanceof ToolItem)
        {
            return ((ToolItem) stack.getItem()).getMaterial().getAttackDamage() > 4.0f;
        }

        return false;
    }

    public static int findAntiWeakness()
    {
        int slot = -1;
        for (int i = 8; i > -1; i--)
        {
            if (DamageUtil.canBreakWeakness(
                    mc.player.getInventory().getStack(i)))
            {
                slot = i;
                if (mc.player.getInventory().selectedSlot == i)
                {
                    break;
                }
            }
        }

        return slot;
    }

    /**
     * Returns the durability for the given ItemStack.
     *
     * @param stack the stack.
     * @return durability of the stack.
     */
    public static int getDamage(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getDamage();
    }

    /**
     * Returns the durability in percent
     * for the given ItemStack.
     *
     * @param stack the stack.
     * @return durability% of the stack.
     */
    public static float getPercent(ItemStack stack)
    {
        return (getDamage(stack) / (float) stack.getMaxDamage()) * 100.0f;
    }

    /**
     * Convenience method, calls
     * {@link DamageUtil#calculate(Entity, LivingEntity)}
     * for mc.player.
     */
    public static float calculate(Entity crystal)
    {
        return calculate(
                crystal.getX(),
                crystal.getY(),
                crystal.getZ(),
                RotationUtil.getRotationPlayer());
    }

    public static float calculate(Entity crystal,
                                  LivingEntity player,
                                  ClientWorld world)
    {
        return calculate(
            crystal.getX(),
            crystal.getY(),
            crystal.getZ(),
            player.getBoundingBox(),
            player,
            world,
            false,
            false);
    }

    /**
     * Convenience method, calls
     * {@link DamageUtil#calculate(BlockPos, LivingEntity)}
     * for mc.player and x + 0.5f, y + 1.0, z + 0.5f.
     */
    public static float calculate(BlockPos pos)
    {
        // Using floats for the offsets here is really important.
        // That is the only reason why this works near the WorldBorder.
        return calculate(pos.getX() + 0.5f,
                         pos.getY() + 1,
                         pos.getZ() + 0.5f,
                         RotationUtil.getRotationPlayer());
    }

    /**
     * Convenience method, calls
     * {@link DamageUtil#calculate(double, double, double, LivingEntity)}
     * for the block p, x + 0.5, y + 1, z + 0.5;
     */
    public static float calculate(BlockPos p, LivingEntity base)
    {
        return calculate(p.getX() + 0.5f, p.getY() + 1, p.getZ() + 0.5f, base);
    }

    /**
     * Convenience method, calls
     * {@link DamageUtil#calculate(double, double, double,
     * Box, LivingEntity, ClientWorld, boolean)}
     */
    public static float calculate(BlockPos p,
                                  LivingEntity base,
                                  ClientWorld world)
    {
        return calculate(p.getX() + 0.5f, p.getY() + 1, p.getZ() + 0.5f,
                         base.getBoundingBox(), base, world, false);
    }

    /**
     * Convenience method, calls
     * {@link DamageUtil#calculate(double, double, double, LivingEntity)}
     * for for the entities position.
     */
    public static float calculate(Entity crystal, LivingEntity base)
    {
        return calculate(crystal.getX(), crystal.getY(), crystal.getZ(), base);
    }

    /**
     * Calculates the damage an explosion of size 6.0 (Endcrystal) would deal
     * to the targeted LivingEntity. Note that beds(0.5, 0.5, 0.5) explode
     * with a different offset than crystals(0.5, 1, 0.5) at their headpiece.
     * Beds also don't create the same explosion size, but we ignore that for
     * the sake of minDamage settings being the same for all calculations.
     * (FeetPlace does same damage anyways).
     *
     * @param x the x coordinate of the position.
     * @param y the y coordinate of the position.
     * @param z the z coordinate of the position.
     * @param base the targeted entity.
     * @return damage dealt to the entity.
     */
    public static float calculate(double x,
                                  double y,
                                  double z,
                                  LivingEntity base)
    {
        return calculate(x, y, z, base.getBoundingBox(), base);
    }

    public static float calculate(double x,
                                  double y,
                                  double z,
                                  Box bb,
                                  LivingEntity base)
    {
        return calculate(x, y, z, bb, base, false);
    }

    public static float calculate(double x,
                                  double y,
                                  double z,
                                  Box bb,
                                  LivingEntity base,
                                  boolean terrainCalc)
    {
        return calculate(x, y, z, bb, base, mc.world, terrainCalc);
    }

    public static float calculate(double x,
                                  double y,
                                  double z,
                                  Box bb,
                                  LivingEntity base,
                                  ClientWorld world,
                                  boolean terrainCalc)
    {
        return calculate(x, y, z, bb, base, world, terrainCalc, false);
    }

    public static float calculate(double x,
                                  double y,
                                  double z,
                                  Box bb,
                                  LivingEntity base,
                                  ClientWorld world,
                                  boolean terrainCalc,
                                  boolean anvils)
    {
        return calculate(x, y, z, bb, base, world, terrainCalc, anvils, 6.0f);
    }

    /**
     * Explosion calculation with power being variable so that it can be accurately calculated for beds.
     * In trying to make an accurate bed aura that can be useful on a variety of servers, this is important.
     *
     * @return damage dealt by an explosion of the given size.
     */
    public static float calculate(double x,
                                  double y,
                                  double z,
                                  Box bb,
                                  LivingEntity base,
                                  ClientWorld world,
                                  boolean terrainCalc,
                                  boolean anvils,
                                  float power)
    {
        if (bb == null || base == null || world == null) {
            return 0.0F;
        }
        double bX = bb.minX + (bb.maxX - bb.minX) * 0.5;
        double bZ = bb.minZ + (bb.maxZ - bb.minZ) * 0.5;
        double distance =
            Math.sqrt(DistanceUtil.distanceSq(x, y, z, bX, bb.minY, bZ)) / 12.0;
        if (distance > 1.0)
        {
            return 0.0F;
        }

        double density = getBlockDensity(new Vec3d(x, y, z),
                bb,
                world,
                true,
                true,
                terrainCalc,
                anvils);

        double densityDistance = distance = (1.0 - distance) * density;
        float damage = getDifficultyMultiplier((float)
                ((densityDistance * densityDistance + distance)
                        / 2.0 * 7.0 * 12.0 + 1.0));

        DamageSource damageSource = new Explosion(mc.world, mc.player, x, y, z, power, false, Explosion.DestructionType.DESTROY).getDamageSource();

        ICachedDamage cache = (ICachedDamage) base;
        int armorValue = cache.getArmorValue();
        float toughness = cache.getArmorToughness();

        damage = CombatRules.getDamageAfterAbsorb(
                damage,
                armorValue,
                toughness);
        
        StatusEffectInstance resistance =
                base.getActiveStatusEffects().get(StatusEffects.RESISTANCE);

        if (resistance != null)
        {
            damage = damage *
                    ((float) (25 - (resistance.getAmplifier() + 1) * 5)) / 25.0f;
        }

        if (damage <= 0.0f)
        {
            return 0.0f;
        }

        int modifierDamage = cache.getExplosionModifier(damageSource);
        if (modifierDamage > 0)
        {
            damage = CombatRules
                    .getDamageAfterMagicAbsorb(damage, modifierDamage);
        }

        return Math.max(damage, 0.0f);
    }

    /**
     * Creates a damage multiplier based on the worlds difficulty
     * and a given distance to a damage source.
     *
     * @param distance the distance to the damage source.
     * @return a damage multiplier.
     */
    public static float getDifficultyMultiplier(float distance)
    {
        switch (mc.world.getDifficulty())
        {
            case PEACEFUL:
                return 0.0F;
            case EASY:
                return Math.min(distance / 2.0f + 1.0f, distance);
            case HARD:
                return distance * 3.0f / 2.0f;
        }

        return distance;
    }

    /**
     * Essentially the same as
     * {@link net.minecraft.world.World#getBlockState(BlockPos)} // idk if this is the right link but alr
     * but with an option to ignore webs and beds.
     *
     * @param vec we check the blocks along this vector.
     * @param bb the bounding box inside which we check.
     * @param ignoreWebs if you want to ignore webs.
     * @return the percentage of real blocks within the given parameters.
     */
    public static float getBlockDensity(Vec3d vec,
                                        Box bb,
                                        ClientWorld world,
                                        boolean ignoreWebs,
                                        boolean ignoreBeds,
                                        boolean terrainCalc,
                                        boolean anvils)
    {
        double x = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double y = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double z = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double xFloor = (1.0 - Math.floor(1.0 / x) * x) / 2.0;
        double zFloor = (1.0 - Math.floor(1.0 / z) * z) / 2.0;

        if (x >= 0.0D && y >= 0.0D && z >= 0.0D)
        {
            int air = 0;
            int traced = 0;

            for (float a = 0.0F; a <= 1.0F; a = (float) (a + x))
            {
                for (float b = 0.0F; b <= 1.0F; b = (float) (b + y))
                {
                    for (float c = 0.0F; c <= 1.0F; c = (float) (c + z))
                    {
                        double xOff = bb.minX + (bb.maxX - bb.minX) * a;
                        double yOff = bb.minY + (bb.maxY - bb.minY) * b;
                        double zOff = bb.minZ + (bb.maxZ - bb.minZ) * c;

                        RayTraceResult result = rayTraceBlocks(
                                new Vec3d(xOff + xFloor, yOff, zOff + zFloor),
                                vec,
                                world,
                                false,
                                false,
                                false,
                                ignoreWebs,
                                ignoreBeds,
                                terrainCalc,
                                anvils);

                        if (result == null)
                        {
                            air++;
                        }

                        traced++;
                    }
                }
            }

            return (float) air / (float) traced;
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Calls {@link RayTracer#
     * trace(World, ClientWorld, Vec3d, Vec3d, boolean, boolean, boolean)}
     *
     * @param start same as the original param.
     * @param end same as the original param.
     * @param stopOnLiquid same as the original param.
     * @param ignoreNoBox same as the original param.
     * @param lastUncollidableBlock same as the original param.
     * @param ignoreWebs handles webs like air.
     * @param ignoreBeds handles beds like air.
     * @return a RayTraceResult...
     */
    @SuppressWarnings("deprecation")
    public static RayTraceResult rayTraceBlocks(Vec3d start,
                                     Vec3d end,
                                     ClientWorld world,
                                     boolean stopOnLiquid,
                                     boolean ignoreNoBox,
                                     boolean lastUncollidableBlock,
                                     boolean ignoreWebs,
                                     boolean ignoreBeds,
                                     boolean terrainCalc,
                                     boolean anvils)
    {
        return RayTracer.trace(mc.world,
                               world,
                               start,
                               end,
                               stopOnLiquid,
                               ignoreNoBox,
                               lastUncollidableBlock,
                               (b, p) ->
                                   !(terrainCalc
                                       && b.getBlastResistance()
                                           < 100
                                       && p.getSquaredDistance(end.x, end.y, end.z)
                                           <= 36.0
                                    || ignoreBeds && b instanceof BedBlock
                                    || ignoreWebs && b instanceof CobwebBlock)
                                    || anvils && b instanceof AnvilBlock);
    }

}

class CombatRules
{
    public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute)
    {
        float f = 2.0F + toughnessAttribute / 4.0F;
        float f1 = MathHelper.clamp(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
        return damage * (1.0F - f1 / 25.0F);
    }

    public static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers)
    {
        float f = MathHelper.clamp(enchantModifiers, 0.0F, 20.0F);
        return damage * (1.0F - f / 25.0F);
    }
}
