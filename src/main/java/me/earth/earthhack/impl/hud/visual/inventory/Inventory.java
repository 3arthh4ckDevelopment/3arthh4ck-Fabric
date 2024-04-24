package me.earth.earthhack.impl.hud.visual.inventory;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
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


    private void render(DrawContext context) {
        if (mc.player != null) {
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
    }

    protected void renderItems(DrawContext context, final DefaultedList<ItemStack> items, final int x, final int y, boolean xCarry) {
        for (int i = 0; i < items.size() - 9; i++) {
            int iX = x + (i % 9) * (18);
            int iY = y + (i / 9) * (18);
            ItemStack itemStack = items.get(i + 9);
            HudRenderUtil.drawItemStack(context, itemStack, iX, iY, true);
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                int iX = x + ((i + 4) % 9) * (18);
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (itemStack != null && !itemStack.isEmpty()) {
                    HudRenderUtil.drawItemStack(context, itemStack, iX, y - 18, true);
                }
            }
        }
    }

    public Inventory() {
        super("Inventory",  HudCategory.Visual, 2, 5);
        this.setData(new SimpleHudData(this, "Displays your inventory"));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
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
