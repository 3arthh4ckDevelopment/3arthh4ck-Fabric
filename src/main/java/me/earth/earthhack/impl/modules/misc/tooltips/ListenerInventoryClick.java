package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.InventoryRenderEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerItemsData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.slot.SlotActionType;

public class ListenerInventoryClick extends ModuleListener<ToolTips, InventoryRenderEvent.InventoryClickEvent> {

    public ListenerInventoryClick(ToolTips module) {
        super(module, InventoryRenderEvent.InventoryClickEvent.class);
    }

    @Override
    public void invoke(InventoryRenderEvent.InventoryClickEvent event) {
        if (module.mode.getValue() == ToolTips.Mode.Hover || mc.player == null || mc.world == null) {
            return;
        }

        if (!(mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof CreativeInventoryScreen)) {
            return;
        }

        double x = event.getX();
        double y = event.getY();

        int scrollAmount = module.scrollAmount;
        int xPosition = (module.mode.getValue() == ToolTips.Mode.Left ? 7 : mc.getWindow().getScaledWidth() - 7 - 9 * 18);
        int startY = 0;
        int width = 9 * 18;
        int height = 58;
        for (ShulkerItemsData data : module.itemsDataList) {
            if (Render2DUtil.mouseWithinBounds(x, y, xPosition, scrollAmount + startY, width, height)) {
                int slot = data.slot();
                if (data.slot() >= 0 && data.slot() < 9) {
                    slot += 36;
                }
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
                return;
            }
            startY += 58;
        }
    }
}
