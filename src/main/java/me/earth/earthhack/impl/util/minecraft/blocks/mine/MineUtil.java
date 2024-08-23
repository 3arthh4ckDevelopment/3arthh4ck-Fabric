package me.earth.earthhack.impl.util.minecraft.blocks.mine;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class MineUtil implements Globals
{

    public static boolean canHarvestBlock(BlockPos pos, ItemStack stack)
    {
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();
        if (state.isToolRequired())
        {
            return true;
        }

        if (stack.isEmpty())
        {
            return stack.getItem().isCorrectForDrops(stack, state); //TODO: check
        }

        return false;
    }

    public static int findBestTool(BlockPos pos)
    {
        return findBestTool(pos, mc.world.getBlockState(pos));
    }

    public static int findBestTool(BlockPos pos, BlockState state)
    {
        int result = mc.player.getInventory().selectedSlot;
        if (state.getHardness(mc.world, pos) > 0)
        {
            double speed = getSpeed(state, mc.player.getMainHandStack());
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.getInventory().getStack(i);
                double stackSpeed = getSpeed(state, stack);
                if (stackSpeed > speed)
                {
                    speed  = stackSpeed;
                    result = i;
                }
            }
        }

        return result;
    }

    public static double getSpeed(BlockState state, ItemStack stack)
    {
        double str = stack.getMiningSpeedMultiplier(state);
    int effect = EnchantmentUtil.getLevel(Enchantments.EFFICIENCY, stack);
        return Math.max(str + (str > 1.0 ? (effect * effect + 1.0) : 0.0), 0.0);
    }

    public static float getDamage(ItemStack stack, BlockPos pos, boolean onGround)
    {
        BlockState state = mc.world.getBlockState(pos);
        return getDamage(state, stack, pos, onGround);
    }

    public static float getDamage(ItemStack stack, BlockPos pos,
                                  boolean onGround, boolean isOnGround)
    {
        BlockState state = mc.world.getBlockState(pos);
        return getDamage(state, stack, pos, onGround, isOnGround);
    }

    public static float getDamage(BlockState state, ItemStack stack,
                                  BlockPos pos, boolean onGround)
    {
        return getDigSpeed(stack, state, onGround, true)
                / (state.getHardness(mc.world, pos)
                    * (canHarvestBlock(pos, stack) ? 30 : 100));
    }

    public static float getDamage(BlockState state, ItemStack stack,
                                  BlockPos pos, boolean onGround,
                                  boolean isOnGround)
    {
        return getDigSpeed(stack, state, onGround, isOnGround)
                / (state.getHardness(mc.world, pos)
                    * (canHarvestBlock(pos, stack) ? 30 : 100));
    }

    private static float getDigSpeed(ItemStack stack, BlockState state,
                                     boolean onGround, boolean isOnGround)
    {
        float digSpeed = 1.0F;

        if (!stack.isEmpty())
        {
            digSpeed *= stack.getMiningSpeedMultiplier(state);
        }

        if (digSpeed > 1.0F)
        {
            int i = EnchantmentUtil.getLevel(Enchantments.EFFICIENCY, stack);

            if (i > 0 && !stack.isEmpty())
            {
                digSpeed += (float)(i * i + 1);
            }
        }

        if (mc.player.hasStatusEffect(StatusEffects.HASTE))
        {
            //noinspection ConstantConditions
            digSpeed *= 1.0F 
                + (mc.player.getStatusEffect(StatusEffects.HASTE)
                            .getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE))
        {
            float miningFatigue;
            //noinspection ConstantConditions
            switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE)
                             .getAmplifier())
            {
                case 0:
                    miningFatigue = 0.3F;
                    break;
                case 1:
                    miningFatigue = 0.09F;
                    break;
                case 2:
                    miningFatigue = 0.0027F;
                    break;
                case 3:
                default:
                    miningFatigue = 8.1E-4F;
            }

            digSpeed *= miningFatigue;
        }

        if (mc.player.isInsideWaterOrBubbleColumn()
                && !EnchantmentUtil.has(Enchantments.AQUA_AFFINITY, EquipmentSlot.HEAD, mc.player))
        {
            digSpeed /= 5.0F;
        }

        if (onGround && (!isOnGround || !mc.player.isOnGround()))
        {
            digSpeed /= 5.0F;
        }

        return (digSpeed < 0 ? 0 : digSpeed);
    }

    public static boolean canBreak(BlockPos pos)
    {
        return canBreak(mc.world.getBlockState(pos), pos);
    }

    public static boolean canBreak(BlockState state, BlockPos pos)
    {
        return state.getHardness(mc.world, pos) != -1
                && state.getBlock() != Blocks.AIR
                && !state.isLiquid();
    }

    public static MineSlots getSlots(boolean onGroundCheck)
    {
        int bestBlock = -1;
        int bestTool  = -1;
        float maxSpeed = 0.0f;
        for (int i = 8; i > -1; i--)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem item)
            {
                Block block = item.getBlock();
                int tool = MineUtil.findBestTool(BlockPos.ORIGIN,
                        block.getDefaultState());
                float damage = MineUtil.getDamage(
                        block.getDefaultState(),
                        mc.player.getInventory().getStack(tool),
                        BlockPos.ORIGIN,
                        !onGroundCheck
                                || RotationUtil.getRotationPlayer().onGround);

                if (damage > maxSpeed)
                {
                    bestBlock = i;
                    bestTool  = tool;
                    maxSpeed  = damage;
                }
            }
        }

        return new MineSlots(bestBlock, bestTool, maxSpeed);
    }
}
