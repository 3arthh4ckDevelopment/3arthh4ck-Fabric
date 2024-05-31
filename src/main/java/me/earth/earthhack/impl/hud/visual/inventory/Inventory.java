package me.earth.earthhack.impl.hud.visual.inventory;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class Inventory extends HudElement {

    private final Setting<Boolean> xCarry =
            register(new BooleanSetting("XCarry", false));
    private final Setting<HudBox> box =
            register(new EnumSetting<>("Box", HudBox.Regular));
    private final Setting<Color> boxColor =
            register(new ColorSetting("BoxColor", new Color(23,23,23,23)));
    private final Setting<Color> outlineColor =
            register(new ColorSetting("OutlineColor", new Color(23,23,23,23)));


    protected void onRender(DrawContext context) {
        if (box.getValue() != HudBox.None) {
            if (box.getValue().equals(HudBox.Rounded))
                Render2DUtil.roundedRect(context.getMatrices(), getX(), getY() - 1.0f,
                        getX() + 9 * 18, getY() + 55.0f, 2, boxColor.getValue().getRGB());
            else
                Render2DUtil.drawBorderedRect(context.getMatrices(), getX(), getY(), getX() + 9 * 18,
                        getY() + 55.0f, 1.0f, boxColor.getValue().getRGB(), outlineColor.getValue().getRGB());
        }

        renderItems(context, mc.player.getInventory().main, (int) getX(), (int) getY(), xCarry.getValue());
    }

    private void renderItems(DrawContext context, DefaultedList<ItemStack> items, int x, int y, boolean xCarry) {
        for (int i = 0; i < items.size() - 9; i++) {
            int iX = x + (i % 9) * (18);
            int iY = y + (i / 9) * (18);
            ItemStack itemStack = items.get(i + 9);
            if (!itemStack.isEmpty())
                Render2DUtil.drawItem(context, itemStack, iX, iY, getZ());
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                int iX = x + ((i + 4) % 9) * (18);
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (!itemStack.isEmpty())
                    Render2DUtil.drawItem(context, itemStack, iX, y, getZ());
            }
        }
    }

    public Inventory() {
        super("Inventory", "Displays your inventory",  HudCategory.Visual, 80, 100);
    }

    @Override
    public float getWidth() {
        return 9 * 18;
    }

    @Override
    public float getHeight() {
        return 55.0f;
    }

    public enum HudBox {
        Regular,
        Rounded,
        None
    }
}
