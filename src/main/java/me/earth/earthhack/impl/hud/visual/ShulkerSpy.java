package me.earth.earthhack.impl.hud.visual;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerItemsData;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerNBTUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShulkerSpy extends HudElement {

    private final Setting<Color> boxColor =
            register(new ColorSetting("BoxColor", new Color(32, 32, 32, 100)));
    private final Setting<Boolean> groundShulkers =
            register(new BooleanSetting("Ground", true));
    private final Setting<Boolean> shulkerSpy =
            register(new BooleanSetting("Holding", true));

    private final Map<ShulkerItemsData, Integer> itemsDataMap = new HashMap<>();

    @Override
    protected void onRender(DrawContext context) {
        int y = (int) getY() + 10;
        int x = (int) getX();
        for (Map.Entry<ShulkerItemsData, Integer> entry : itemsDataMap.entrySet()) {
            ShulkerItemsData itemsData = entry.getKey();
            String itemCount = (entry.getValue() != 1 ? " - x" + entry.getValue() : "");

            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 200);
            Managers.TEXT.drawString(context, itemsData.name() + itemCount, x, y - 10, 0xffffffff);
            context.getMatrices().pop();

            Render2DUtil.drawRect(context.getMatrices(), x, y - 10, x + 9 * 18, y, boxColor.getValue().brighter().getRGB());
            Render2DUtil.drawBorderedRect(context.getMatrices(), x, y, x + 9 * 18, y + 55.0f, 0.5f, boxColor.getValue().getRGB(), itemsData.color());

            Render2DUtil.drawItemsInventory(context, itemsData.itemStackDefaultedList(), x, y);
            y += 70;
        }
    }

    public ShulkerSpy() {
        super("ShulkerSpy", "Shows the contents of a shulker box", HudCategory.Visual, 100, 100);

        this.listeners.add(new LambdaListener<>(TickEvent.class, event -> {
            if (mc.player == null || mc.world == null) {
                return;
            }

            itemsDataMap.clear();
            List<ShulkerItemsData> data = new ArrayList<>();
            for (Entity entity : Managers.ENTITIES.getEntities()) {
                if (groundShulkers.getValue() && entity instanceof ItemEntity item) {
                    ItemStack stack = item.getStack();
                    DefaultedList<ItemStack> items = ShulkerNBTUtil.getShulkerItemList(stack);
                    if (items != null) {
                        data.add(new ShulkerItemsData(items, stack.getName().getString()));
                    }
                }

                if (shulkerSpy.getValue() && entity instanceof PlayerEntity player) {
                    ItemStack stack = player.getInventory().getMainHandStack();
                    DefaultedList<ItemStack> items = ShulkerNBTUtil.getShulkerItemList(stack);
                    if (items != null) {
                        data.add(new ShulkerItemsData(items, player.getName().getString()));
                    }
                }
            }

            data.sort((x, y) ->
                    Integer.compare(
                            x.itemStackDefaultedList().stream().filter(k -> !k.isEmpty()).toList().size(),
                            y.itemStackDefaultedList().stream().filter(k -> !k.isEmpty()).toList().size())
            );

            // merge shulker data
            for (ShulkerItemsData itemsData : data) {
                if (itemsDataMap.containsKey(itemsData)) {
                    itemsDataMap.put(itemsData, itemsDataMap.get(itemsData) + 1);
                } else {
                    itemsDataMap.put(itemsData, 1);
                }
            }
        }));
    }

    @Override
    public float getWidth() {
        return 9 * 18;
    }

    @Override
    public float getHeight() {
        return itemsDataMap.size() * 70;
    }
}
