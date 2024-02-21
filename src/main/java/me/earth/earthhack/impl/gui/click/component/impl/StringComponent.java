package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;

public class StringComponent extends SettingComponent<String, StringSetting> {
    private final StringSetting stringSetting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");
    private boolean idling;
    private final StopWatch idleTimer = new StopWatch();

    public StringComponent(StringSetting stringSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(stringSetting.getName(), posX, posY, offsetX, offsetY, width, height, stringSetting);
        this.stringSetting = stringSetting;
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

        String string = getStringSetting().isPassword() ? getStringSetting().censor() : isListening ? currentString.getString() : getStringSetting().getValue();
        if (isListening) {
            string += getIdleSign();
        }

        drawStringWithShadow(string, getFinishedX() + 6.5f, getFinishedY() + getHeight() - Managers.TEXT.getStringHeightI() - 1f, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void charTyped(char character, int keyCode) {
        super.charTyped(character, keyCode);
        if (isListening && isAllowedCharacter(character)) {
            setString(currentString.getString() + character);
        }
    }

    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (isListening) {
            if (keyCode == 1) {
                return;
            }
            if (keyCode == 28) {
                enterString();
                setListening(false);
            } else if (keyCode == 14) {
                setString(removeLastChar(currentString.getString()));
            } else {
                if (keyCode == Keyboard.getKeyV() && (Keyboard.isKeyDown(Keyboard.getRControl()) || Keyboard.isKeyDown(Keyboard.getLControl()))) {
                    try {
                        setString(currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered && mouseButton == 0)
            toggle();
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public String getIdleSign() {
        if (idleTimer.passed(500)) {
            idling = !idling;
            idleTimer.reset();
        }

        if (idling) {
            return "_";
        }
        return "";
    }

    private void enterString() {
        if (currentString.getString().isEmpty()) {
            getStringSetting().setValue(getStringSetting().getInitial());
        } else {
            getStringSetting().setValue(currentString.getString());
        }
        setString("");
    }

    public StringSetting getStringSetting() {
        return stringSetting;
    }

    public void toggle() {
        isListening = !isListening;
    }

    public boolean getState() {
        return !isListening;
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public static boolean isAllowedCharacter(char character)
    {
        return character != 167 && character >= ' ' && character != 127;
    }

}
