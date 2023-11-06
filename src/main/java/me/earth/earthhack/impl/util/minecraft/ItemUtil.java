package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;

public class ItemUtil implements Globals
{
    public static boolean isThrowable(Item item)
    {
        return item instanceof EnderPearlItem
                || item instanceof ExperienceBottleItem
                || item instanceof SnowballItem
                || item instanceof EggItem
                || item instanceof SplashPotionItem
                || item instanceof LingeringPotionItem
                || item instanceof FishingRodItem;
    }

    public static boolean areSame(Block block1, Block block2)
    {
        return Block.getRawIdFromState(block1.getDefaultState()) == Block.getRawIdFromState(block2.getDefaultState());
    }

    public static boolean areSame(Item item1, Item item2)
    {
        return Item.getRawId(item1)
                == Item.getRawId(item2);
    }

    public static boolean areSame(Block block, Item item)
    {
        return item instanceof BlockItem
                && areSame(block, ((BlockItem) item).getBlock());
    }

    public static boolean areSame(ItemStack stack, Block block)
    {
        if (stack == null)
        {
            return false;
        }

        if (block == Blocks.AIR && stack.isEmpty())
        {
            return true;
        }

        return areSame(block, stack.getItem());
    }

    public static boolean areSame(ItemStack stack, Item item)
    {
        return stack != null && areSame(stack.getItem(), item);
    }

}
