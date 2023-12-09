package me.earth.earthhack.impl.util.minecraft.blocks.mine;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.block.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;

public class MineUtil implements Globals
{

    public static boolean canHarvestBlock(BlockPos pos, ItemStack stack)
    {
        BlockState state = mc.world.getBlockState(pos);
        state = state.getActualState(mc.world, pos);
        Block block = state.getBlock();
        if (state.isToolRequired())
        {
            return true;
        }

        if (stack.isEmpty())
        {
            return stack.canHarvestBlock(state);
        }

        String tool = ((IBlock) block).getHarvestToolNonForge(state);
        if (tool == null)
        {
            return stack.canHarvestBlock(state);
        }

        int toolLevel = -1;
        if (stack.getItem() instanceof ToolItem)
        {
            String toolClass = null;
            if (stack.getItem() instanceof PickaxeItem)
            {
                toolClass = "pickaxe";
            }
            else if (stack.getItem() instanceof AxeItem)
            {
                toolClass = "axe";
            }
            else if (stack.getItem() instanceof SwordItem)
            {
                toolClass = "shovel";
            }
            
            if (tool.equals(toolClass))
            {
                toolLevel = ((ToolItem) stack.getItem()).getMaterial()
                                                         .getHarvestLevel();
            }
        }

        if (toolLevel < 0)
        {
            return stack.canHarvestBlock(state);
        }

        return toolLevel >= ((IBlock) block).getHarvestLevelNonForge(state);
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
    int effect = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
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
            int i = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);

            if (i > 0 && !stack.isEmpty())
            {
                digSpeed += (float)(i * i + 1);
            }
        }

        if (mc.player.isPotionActive(StatusEffects.HASTE))
        {
            //noinspection ConstantConditions
            digSpeed *= 1.0F 
                + (mc.player.getActivePotionEffect(StatusEffects.HASTE)
                            .getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(StatusEffects.MINING_FATIGUE))
        {
            float miningFatigue;
            //noinspection ConstantConditions
            switch (mc.player.getActivePotionEffect(StatusEffects.MINING_FATIGUE)
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

        if (mc.player.isInsideOfMaterial(Material.WATER)
                && !EnchantmentHelper.getAquaAffinityModifier(mc.player))
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

}
