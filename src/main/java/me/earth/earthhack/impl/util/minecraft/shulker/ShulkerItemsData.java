package me.earth.earthhack.impl.util.minecraft.shulker;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class ShulkerItemsData {

    public final DefaultedList<ItemStack> itemStackDefaultedList;
    public final String name;
    public int color = new Color(153, 83, 176).getRGB();
    public int slot = -1;

    public ShulkerItemsData(DefaultedList<ItemStack> itemStackDefaultedList, String name) {
        this.itemStackDefaultedList = itemStackDefaultedList;
        this.name = name;
    }

    public ShulkerItemsData(DefaultedList<ItemStack> itemStackDefaultedList, String name, int color) {
        this(itemStackDefaultedList, name);
        this.color = color;
    }

    public ShulkerItemsData(DefaultedList<ItemStack> itemStackDefaultedList, String name, int color, int slot) {
        this(itemStackDefaultedList, name, color);
        this.slot = slot;
    }

    public DefaultedList<ItemStack> itemStackDefaultedList() {
        return itemStackDefaultedList;
    }

    public String name() {
        return name;
    }

    public int color() {
        return color;
    }

    public int slot() {
        return slot;
    }

    private boolean areItemStackListsEqual(DefaultedList<ItemStack> list1, DefaultedList<ItemStack> list2) {
        if ((list1 == null || list2 == null) || list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            ItemStack item1 = list1.get(i);
            ItemStack item2 = list2.get(i);
            if (!ItemStack.areEqual(item1, item2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ShulkerItemsData itemsData = (ShulkerItemsData) obj;
        return areItemStackListsEqual(itemStackDefaultedList, itemsData.itemStackDefaultedList) && name.equals(itemsData.name);
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
