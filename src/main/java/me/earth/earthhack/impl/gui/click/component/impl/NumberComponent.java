package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class NumberComponent extends SettingComponent<Number, NumberSetting<Number>> {
    private final NumberSetting<Number> numberSetting;
    private boolean sliding;

    public NumberComponent(NumberSetting<Number> numberSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(numberSetting.getName(), posX, posY, offsetX, offsetY, width, height, numberSetting);
        this.numberSetting = numberSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        final boolean hovered = Render2DUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        drawStringWithShadow(getLabel() + ": " + Formatting.GRAY + getNumberSetting().getValue(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);
        float length = MathHelper.floor(((getNumberSetting().getValue()).floatValue() - getNumberSetting().getMin().floatValue()) / (getNumberSetting().getMax().floatValue() - getNumberSetting().getMin().floatValue()) * (getWidth() - 10));
        if (getClickGui().get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 5, getFinishedY() + getHeight() - 2.5f, getFinishedX() + 5 + length, getFinishedY() + getHeight() - 0.5f, 0.5f, hovered ? getClickGui().get().getSettingColor().brighter().getRGB() : getClickGui().get().getSettingColor().getRGB(), 0xff000000);
        else
            Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 5, getFinishedY() + getHeight() - 2.3f, getFinishedX() + 5 + length, getFinishedY() + getHeight() - 0.3f, 0xffffffff);
        if (sliding) {
            double val = ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMax().doubleValue() - getNumberSetting().getMin().doubleValue()) / (getWidth() - 10) + getNumberSetting().getMin().doubleValue());
            getNumberSetting().setValue(getNumberSetting().numberToValue(MathUtil.round(val, 2)));
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = Render2DUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered && mouseButton == 0)
            setSliding(true);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isSliding())
            setSliding(false);
    }

    public NumberSetting<Number> getNumberSetting() {
        return numberSetting;
    }

    public boolean isSliding() {
        return sliding;
    }

    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }
}
