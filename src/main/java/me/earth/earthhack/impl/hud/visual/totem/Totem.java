package me.earth.earthhack.impl.hud.visual.totem;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.gui.hud.HudEditorGui;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Totem extends HudElement {

    private void render(DrawContext context) {
        int totems = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);

        context.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING), (int) getX(), (int) getY(), 100206, (int) getZ());

        if (totems <= 0 && mc.currentScreen instanceof HudEditorGui) {
            HudRenderUtil.renderText(context, "0", (int) getX() + 17 - RENDERER.getStringWidth("0"), (int) getY() + 9);
        } else {
            HudRenderUtil.renderText(context, String.valueOf(totems), (int) getX() + 17 - RENDERER.getStringWidth(String.valueOf(totems)), (int) getY() + 9);
        }
    }

    public Totem() {
        super("Totem", HudCategory.Visual, 120, 120);
        this.setData(new SimpleHudData(this, "Displays your totems."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void hudDraw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate() {
        super.hudUpdate();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return 18.0f;
    }

    @Override
    public float getHeight() {
        return 18.0f;
    }

}
