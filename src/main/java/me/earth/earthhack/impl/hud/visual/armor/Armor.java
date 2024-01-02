package me.earth.earthhack.impl.hud.visual.armor;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.render.ColorHelper;
import net.minecraft.item.ItemStack;

// TODO: vertical mode

public class Armor extends HudElement {

    private final Setting<Boolean> durability =
            register(new BooleanSetting("Durability", true));

    private void render() {
        if (mc.player != null) {
            //TODO: scale this ?
            float x = getX();
            for (ItemStack stack : mc.player.getInventory().armor) {
                if (!stack.isEmpty()) {
                    if (durability.getValue()) {
                        final float percent = DamageUtil.getPercent(stack) / 100.0f;
                        Managers.TEXT.drawStringWithShadow(
                                ((int) (percent * 100.0f)) + "%", (getX() + x + 2) * 1.6f, (getY() + 1) * 1.6f, ColorHelper.toColor(percent * 120.0f, 100.0f, 50.0f, 1.0f).getRGB());
                    }
                    getContext().drawItem(stack, (int) x, (int) getY());
                    getContext().drawItemInSlot(mc.textRenderer, stack, (int) x, (int) getY());
                    x += 18;
                }
            }
        }
    }

    public Armor() {
        super("Armor", HudCategory.Visual, 120, 420);
        this.setData(new SimpleHudData(this, "Displays your armor."));
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
        return 72.0f;
    }

    @Override
    public float getHeight() {
        return 20.0f;
    }

}
