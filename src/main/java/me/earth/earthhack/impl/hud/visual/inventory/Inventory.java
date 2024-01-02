package me.earth.earthhack.impl.hud.visual.inventory;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class Inventory extends HudElement {

    private Setting<Boolean> xCarry =
            register(new BooleanSetting("RenderXCarry", false));
    private Setting<Boolean> box =
            register(new BooleanSetting("Box", true));
    private Setting<Boolean> pretty =
            register(new BooleanSetting("Pretty", true));
    private Setting<Color> boxColor =
            register(new ColorSetting("BoxColor", new Color(23,23,23,23)));
    private Setting<Color> outlineColor =
            register(new ColorSetting("OutlineColor", new Color(23,23,23,23)));


    private void render() {
        if (box.getValue()) {
            if (pretty.getValue())
                Render2DUtil.roundedRect(getContext().getMatrices(), getX(), getY() - 1.0f, getX() + 9 * 18, getY() + 55.0f, 2, boxColor.getValue().getRGB());
            else
                Render2DUtil.drawBorderedRect(getContext().getMatrices(), getX(), getY(), getX() + 9 * 18, getY() + 55.0f, 1.0f, boxColor.getValue().getRGB(), outlineColor.getValue().getRGB());
        }

        ItemRender(mc.player.getInventory().main, (int) getX(),(int) getY(), xCarry.getValue());
    }

    protected void ItemRender(final DefaultedList<ItemStack> items, final int x, final int y, boolean xCarry) {
        for (int i = 0; i < items.size() - 9; i++) {
            int iX = x + (i % 9) * (18);
            int iY = y + (i / 9) * (18);
            ItemStack itemStack = items.get(i + 9);
            getContext().drawItem(itemStack, x, y, 100203, (int) getZ());
            Managers.TEXT.drawString(String.valueOf(itemStack.getCount()), x, y, 0xffffffff);
        }

        if (xCarry) {
            for (int i = 1; i < 5; i++) {
                int iX = x + ((i + 4) % 9) * (18);
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (itemStack != null && !itemStack.isEmpty()) {
                    getContext().drawItem(itemStack, iX, y - 18, 100204, (int) getZ());
                    Managers.TEXT.drawString(String.valueOf(itemStack.getCount()), iX, y - 18, 0xffffffff);
                }
            }
        }
    }

    public Inventory() {
        super("Inventory",  HudCategory.Visual, 2, 5);
        this.setData(new SimpleHudData(this, "Displays your inventory"));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
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

    private enum Mode {
        Box,
        OutLine
    }

}
