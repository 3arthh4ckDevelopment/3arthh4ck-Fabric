package me.earth.earthhack.impl.hud.visual.armor;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class Armor extends HudElement {

    private final Setting<Boolean> durability =
            register(new BooleanSetting("Durability", true));
    private final Setting<Boolean> vertical =
            register(new BooleanSetting("Vertical", false));

    protected void onRender(DrawContext context) {
        float x = getX();
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = mc.player.getInventory().getArmorStack(i);
            final float percent = DamageUtil.getPercent(stack) / 100.0f;
            if (!stack.isEmpty())
            {
                if (durability.getValue()) {
                        context.getMatrices().push();
                        context.getMatrices().scale(0.625f, 0.625f, 0.625f);
                        Managers.TEXT.drawStringWithShadow(context,
                                ((int) (percent * 100.0f)) + "%",
                                (x + 2) * 1.6f, (getY() - 3) * 1.6f - 1,
                                ColorHelper.toColor(percent * 120.0f, 100.0f, 50.0f, 1.0f).getRGB());
                        context.getMatrices().scale(1.0f, 1.0f, 1.0f);
                        context.getMatrices().pop();
                }
                Render2DUtil.drawItem(context, stack, (int) x, (int) getY(), true);
                x += 18;
            }
        }
    }

    public Armor() {
        super("Armor", "Displays your armor.", HudCategory.Visual, 120, 420);
    }

    @Override
    public float getWidth() {
        return 72.0f;
    }

    @Override
    public float getHeight() {
        return 20.0f;
    }
}
