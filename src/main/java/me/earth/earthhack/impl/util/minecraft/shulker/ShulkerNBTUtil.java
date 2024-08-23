package me.earth.earthhack.impl.util.minecraft.shulker;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class ShulkerNBTUtil {

    public static int getShulkerColor(ItemStack stack) {
        ShulkerBoxBlock block = (ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock();
        Color color;
        if (block.getColor() == null) {
            color = new Color(153, 83, 176);
        } else {
            color = new Color(block.getColor().getMapColor().color);
        }

        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255).getRGB();
    }

    public static DefaultedList<ItemStack> getShulkerItemList(ItemStack stack) {
        if (stack.getComponents().isEmpty()) {
            return null;
        }

        NbtComponent nbtComponent = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (nbtComponent == null) {
            return null;
        }
        NbtElement nbtElement = nbtComponent.copyNbt();
        if (!isItemShulkerBox(nbtElement)) {
            return null;
        }

        return getItemsFromNBT(nbtElement);
    }

    private static DefaultedList<ItemStack> getItemsFromNBT(NbtElement element) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(27, ItemStack.EMPTY);
        NbtList list = ((NbtCompound) element).getList("Items", 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound item = list.getCompound(i);
            int slot = item.getByte("Slot");
            if (slot >= 0 && slot < items.size()) {
                items.set(slot, ItemStack.fromNbtOrEmpty(null, item));
            }
        }
        return items;
    }

    private static boolean isItemShulkerBox(NbtElement element) {
        NbtCompound nbt = (NbtCompound) element;
        return nbt != null && nbt.contains("id", 8) && nbt.getString("id").contains("shulker_box");
    }

}

