package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.InventoryRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerItemsData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class ListenerInventoryRender extends ModuleListener<ToolTips, InventoryRenderEvent> {

    public ListenerInventoryRender(ToolTips module) {
        super(module, InventoryRenderEvent.class);
    }

    @Override
    public void invoke(InventoryRenderEvent event) {
        if (module.mode.getValue() == ToolTips.Mode.Hover || mc.player == null || mc.world == null) {
            return;
        }

        if (!(mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof CreativeInventoryScreen)) {
            return;
        }

        int y = module.scrollAmount;
        int x = (module.mode.getValue() == ToolTips.Mode.Left ? 7 : mc.getWindow().getScaledWidth() - 7 - 9 * 18);
        for (ShulkerItemsData data : module.itemsDataList) {
            Render2DUtil.drawBorderedRect(event.getContext().getMatrices(), x, y, x + 9 * 18, y + 55.0f, 1.0f, module.boxColor.getValue().getRGB(), data.color());
            Render2DUtil.drawItemsInventory(event.getContext(), data.itemStackDefaultedList(), x, y);
            y += 58;
        }
    }
}
