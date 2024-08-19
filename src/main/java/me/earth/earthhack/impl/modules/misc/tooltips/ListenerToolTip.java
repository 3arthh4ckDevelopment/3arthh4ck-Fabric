package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerNBTUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ListenerToolTip extends ModuleListener<ToolTips, ToolTipEvent> {

    public ListenerToolTip(ToolTips module) {
        super(module, ToolTipEvent.class);
    }

    @Override
    public void invoke(ToolTipEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (module.maps.getValue() && event.getItemStack().getItem() == Items.FILLED_MAP) {
            event.setCancelled(true);
            renderMap(event);
        } else if (module.mode.getValue() == ToolTips.Mode.Hover) {
            DefaultedList<ItemStack> items = ShulkerNBTUtil.getShulkerItemList(event.getItemStack());
            if (items == null) {
                event.setCancelled(true);
                renderShulker(event, items);
            }
        }
    }

    private void renderShulker(ToolTipEvent event, DefaultedList<ItemStack> items) {
        DrawContext context = event.getContext();
        int x = event.getX();
        int y = event.getY();

        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, 300);
        if (module.vanillaStyle.getValue()) {
            context.drawTexture(Identifier.tryParse("minecraft", "textures/gui/container/shulker_box.png"), x, y, 0, 0, 180, 80);
            x += 8;
            y += 18;
            Managers.TEXT.drawString(context, event.getItemStack().getName().getString(), x, y - 10, Color.DARK_GRAY.getRGB());
        } else {
            int shulkerColor = new Color(40, 40, 40, 100).getRGB();
            if (module.showColor.getValue()) {
                shulkerColor = ((BlockItem) event.getItemStack().getItem()).getBlock().getDefaultMapColor().color;
            }

            Render2DUtil.drawRect(context.getMatrices(), x, y - 10, x + 9 * 18, y, module.boxColor.getValue().brighter().getRGB());
            Render2DUtil.drawBorderedRect(context.getMatrices(), x, y, x + 9 * 18, y + 55.0f, 0.5f, module.boxColor.getValue().getRGB(), shulkerColor);

            Managers.TEXT.drawString(context, event.getItemStack().getName().getString(), x, y - 10, Color.WHITE.getRGB());
        }
        context.getMatrices().pop();
        Render2DUtil.drawItemsInventory(event.getContext(), items, x, y);
    }

    private void renderMap(ToolTipEvent event) {
        if (!event.getItemStack().hasNbt()) {
            return;
        }

        int x = event.getX();
        int y = event.getY();

        MapIdComponent mapIdComponent = event.getItemStack().get(DataComponentTypes.MAP_ID);

        event.getContext().getMatrices().push();
        event.getContext().getMatrices().translate(0.0f, 0.0f, 300.0f);
        event.getContext().drawGuiTexture(Identifier.tryParse("minecraft", "container/cartography_table/map"), x, y, 66, 66);
        drawMap(event.getContext(), mapIdComponent, FilledMapItem.getMapState(event.getItemStack(), mc.world), x + 4, y + 4, 0.45F);
        event.getContext().getMatrices().pop();
    }

    /**
     * {@link net.minecraft.client.gui.screen.ingame.CartographyTableScreen} Drawmap method
     */
    private void drawMap(DrawContext context, @Nullable MapIdComponent mapId, @Nullable MapState mapState, int x, int y, float scale) {
        if (mapId != null && mapState != null) {
            context.getMatrices().push();
            context.getMatrices().translate((float)x, (float)y, 1.0F);
            context.getMatrices().scale(scale, scale, 1.0F);
            mc.gameRenderer.getMapRenderer().draw(context.getMatrices(), context.getVertexConsumers(), mapId, mapState, true, 15728880);
            context.draw();
            context.getMatrices().pop();
        }

    }
}
