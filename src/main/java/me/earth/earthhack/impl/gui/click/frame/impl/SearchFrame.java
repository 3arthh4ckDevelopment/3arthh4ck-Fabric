package me.earth.earthhack.impl.gui.click.frame.impl;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.ModulesRating;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.List;

import static me.earth.earthhack.impl.gui.click.component.impl.StringComponent.isAllowedCharacter;

public class SearchFrame extends Frame implements Globals {
    public static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private StringComponent.CurrentString currentString = new StringComponent.CurrentString("");
    private final StopWatch idleTimer = new StopWatch();
    public boolean isListening, idling;

    public SearchFrame()
    {
        super("Search", CLICK_GUI.get().searchPosX.getValue(), CLICK_GUI.get().searchPosY.getValue(), CLICK_GUI.get().searchWidth.getValue(), 15.5f);
    }

    public void clearInput() {
        setListening(false);
        setString("");
        updateRating();
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        String text = currentString.getString();

        setWidth(CLICK_GUI.get().searchWidth.getValue());
        if (!isListening && text.isEmpty())
            text = "Search for a module...";
        else if (isListening)
            text += getIdleSign();

        if (CLICK_GUI.get().search.getValue() == ClickGui.SearchStyle.Box) {
            if (CLICK_GUI.get().catEars.getValue()) {
                CategoryFrame.catEarsRender(context, getPosX(), getPosY(), getWidth());
            }
            Render2DUtil.drawRect(context.getMatrices(), getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), CLICK_GUI.get().getTopBgColor().getRGB());
            if (CLICK_GUI.get().getBoxes())
                Render2DUtil.drawBorderedRect(context.getMatrices(), getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), 0.5f, CLICK_GUI.get().getModulesColor().getRGB(), CLICK_GUI.get().getTopColor().getRGB());

            drawStringWithShadow(context, getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);
            Render2DUtil.drawRect(context.getMatrices(), getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + (Managers.TEXT.getStringHeightI() + 3), 0x92000000);
            drawStringWithShadow(context, text, getPosX() + 2, getPosY() - getHeight() + 33.5f, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else if (CLICK_GUI.get().search.getValue() == ClickGui.SearchStyle.TextBar) {
            Render2DUtil.roundedRect(context.getMatrices(), getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), 4.0f, 0xff232323);
            drawStringWithShadow(context, text, getPosX() + 10, getPosY() + getHeight() / 2 - Managers.TEXT.getStringHeightI() / 2.0f, getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX() + 2, getPosY() + (CLICK_GUI.get().search.getValue() == ClickGui.SearchStyle.Box ? Managers.TEXT.getStringHeightI() : 0), getWidth(), getHeight());
        if (hovered && mouseButton == 0) {
            toggle();
            if (isListening)
                setString("");
        } else if (!hovered && isListening) {
            toggle();
            setString(currentString.getString());
            updateRating();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isListening) {
            if (keyCode == 1) {
                return;
            }
            if (keyCode == 28) {
                setString(currentString.getString());
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
                    return;
                }
                if (isAllowedCharacter(character)) {
                    setString(currentString.getString() + character);
                }
            }
            updateRating();
        }
    }

    private void updateRating() {
        if (currentString.getString().trim().isEmpty()) {
            for (Module m : Managers.MODULES.getRegistered())
                m.searchVisibility = true;
        }

        ModulesRating rating = new ModulesRating();
        rating.modules.addAll(Managers.MODULES.getRegistered());
        List<Module> visibleModules = rating.modulesVisibility(currentString.getString(), CLICK_GUI.get().precision.getValue(), CLICK_GUI.get().aliases.getValue());

        for (Module module : Managers.MODULES.getRegistered()) {
            module.searchVisibility = visibleModules.contains(module);
        }
        visibleModules.clear();
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        CLICK_GUI.get().searchPosX.setValue(getPosX());
        CLICK_GUI.get().searchPosY.setValue(getPosY());
    }

    private String getIdleSign() {
        if (idleTimer.passed(500)) {
            idling = !idling;
            idleTimer.reset();
        }

        if (idling) {
            return "_";
        }
        return "";
    }

    private void toggle() {
        isListening = !isListening;
    }

    private boolean getState() {
        return !isListening;
    }

    private void setListening(boolean listening) {
        isListening = listening;
    }

    private void setString(String newString) {
        this.currentString = new StringComponent.CurrentString(newString);
    }

    private static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }
}
