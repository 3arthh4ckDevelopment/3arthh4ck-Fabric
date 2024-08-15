package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerItemsData;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerNBTUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ListenerTick extends ModuleListener<ToolTips, TickEvent> {

    public ListenerTick(ToolTips module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (module.mode.getValue() == ToolTips.Mode.Hover || mc.player == null || mc.world == null) {
            return;
        }

        module.itemsDataList.clear();
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = InventoryUtil.get(i);
            DefaultedList<ItemStack> items = ShulkerNBTUtil.getShulkerItemList(itemStack);
            if (items != null) {
                module.itemsDataList.add(new ShulkerItemsData(items, itemStack.getName().getString(), ShulkerNBTUtil.getShulkerColor(itemStack), i));
            }
        }

        // sort by the amount of occupied slots
        module.itemsDataList.sort((x, y) ->
                Integer.compare(
                        x.itemStackDefaultedList().stream().filter(k -> !k.isEmpty()).toList().size(),
                        y.itemStackDefaultedList().stream().filter(k -> !k.isEmpty()).toList().size())
        );
    }
}
