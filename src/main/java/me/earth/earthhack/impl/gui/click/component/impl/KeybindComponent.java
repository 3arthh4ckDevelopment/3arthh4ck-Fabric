package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class KeybindComponent extends SettingComponent<Bind, BindSetting> {
    private final BindSetting bindSetting;
    private boolean binding;

    public KeybindComponent(BindSetting bindSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(bindSetting.getName(), posX, posY, offsetX, offsetY, width, height, bindSetting);
        this.bindSetting = bindSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (getClickGui().get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 4.5f, getFinishedY() + 1.0f, getFinishedX() + getWidth() - 4.5f, getFinishedY() + getHeight() - 0.5f, 0.5f, hovered ? 0x66333333 : 0, 0xff000000);
        else
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 5, getFinishedY() + 1.5f, getFinishedX() + getWidth() - 5.5f, getFinishedY() + getHeight() - 1, 0.5f, hovered ? 0x66333333 : 0, 0xff000000);

        drawStringWithShadow(isBinding() ? "Press a key..." : getBindSetting().getName() + ": " + Formatting.GRAY+ getBindSetting().getValue(), getFinishedX() + 6.5f, getFinishedY() + getHeight() - Managers.TEXT.getStringHeightI() - 1f, 0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isBinding()) {
            final Bind bind = Bind.fromKey(keyCode == Keyboard.getEscape() || keyCode == Keyboard.getSpace() || keyCode == Keyboard.getDelete() ? Keyboard.getNone() : keyCode);
            getBindSetting().setValue(bind);
            setBinding(false);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered && mouseButton == 0)
            setBinding(!isBinding());
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public BindSetting getBindSetting() {
        return bindSetting;
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
