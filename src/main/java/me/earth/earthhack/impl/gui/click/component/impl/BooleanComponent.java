package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public class BooleanComponent extends SettingComponent<Boolean, Setting<Boolean>> {
    private final BooleanSetting booleanSetting;

    public BooleanComponent(BooleanSetting booleanSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(booleanSetting.getName(), posX, posY, offsetX, offsetY, width, height, booleanSetting);
        this.booleanSetting = booleanSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 17,getFinishedY() + 1,12,getHeight() - 2);
        drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getBooleanSetting().getValue() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (getClickGui().get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + getWidth() - 17,getFinishedY() + 1,getFinishedX() + getWidth() - 5,getFinishedY() + getHeight() - 1,0.5f, getBooleanSetting().getValue() ? ( hovered ? getClickGui().get().getModulesColor().brighter().getRGB():getClickGui().get().getModulesColor().getRGB()):(hovered ? 0x66333333:0),0xff000000);
        else
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + getWidth() - 17,getFinishedY() + 1.5f,getFinishedX() + getWidth() - 5.5f,getFinishedY() + getHeight() - 1,0.5f, getBooleanSetting().getValue() ? ( hovered ? getClickGui().get().getModulesColor().brighter().getRGB():getClickGui().get().getModulesColor().getRGB()):(hovered ? 0x66333333:0),0xff000000);

        if (getBooleanSetting().getValue())
            Render2DUtil.drawCheckMark(context.getMatrices(), getFinishedX() + getWidth() - 11,getFinishedY() + 1,10,0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 17,getFinishedY() + 1,12,getHeight() - 2);
        if (hovered && mouseButton == 0)
            getBooleanSetting().setValue(!getBooleanSetting().getValue());
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BooleanSetting getBooleanSetting() {
        return booleanSetting;
    }

}
