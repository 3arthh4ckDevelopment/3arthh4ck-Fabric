package me.earth.earthhack.impl.hud.visual.totem;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Totem extends HudElement {

    protected void onRender(DrawContext context) {
        Render2DUtil.drawItem(context, new ItemStack(Items.TOTEM_OF_UNDYING, InventoryUtil.getCount(Items.TOTEM_OF_UNDYING)), (int) getX(), (int) getY(), true);
    }

    public Totem() {
        super("Totem", "Displays your totems.", HudCategory.Visual, 120, 120);
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
