package me.earth.earthhack.impl.gui.click.frame;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.util.ArrayList;

public class Frame {

    public static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private final String label;
    private float posX;
    private float posY;
    private float lastPosX;
    private float lastPosY;
    private float width;
    private final float height;
    private boolean extended, dragging;
    private final ArrayList<Component> components = new ArrayList<>();
    private int scrollY;

    public Frame(String label, float posX, float posY, float width, float height) {
        this.label = label;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public void init() {
        components.forEach(Component::init);
    }

    public void moved(float posX,float posY) {
        components.forEach(component -> component.moved(posX,posY));
    }

    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Window scaledResolution = MinecraftClient.getInstance().getWindow();
        if (isDragging()) {
            setPosX(mouseX + getLastPosX());
            setPosY(mouseY + getLastPosY());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY() + getScrollY()));
        }
        if (getPosX() < 0) {
            setPosX(0);
            getComponents().forEach(component -> component.moved(getPosX(),getPosY() + getScrollY()));
        }
        if (getPosX() + getWidth() > scaledResolution.getScaledWidth()) {
            setPosX(scaledResolution.getScaledWidth() - getWidth());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY() + getScrollY()));
        }
        if (getPosY() < 0) {
            setPosY(0);
            getComponents().forEach(component -> component.moved(getPosX(),getPosY() + getScrollY()));
        }
        if (getPosY() + getHeight() > scaledResolution.getScaledHeight()) {
            setPosY(scaledResolution.getScaledHeight() - getHeight());
            getComponents().forEach(component -> component.moved(getPosX(),getPosY() + getScrollY()));
        }
    }

    public void charTyped(char character, int keyCode)  {
        if (isExtended()) getComponents().forEach(component -> component.charTyped(character, keyCode));
    }

    public void keyPressed(int keyCode)  {
        if (isExtended()) getComponents().forEach(component -> component.keyPressed(keyCode));
    }

    public void mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (isExtended()) getComponents().forEach(component -> component.mouseScrolled(mouseX, mouseY, scrollAmount));
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY(), getWidth(), getHeight());
        switch (mouseButton) {
            case 0:
                if (hovered) {
                    setDragging(true);
                    setLastPosX((float) (getPosX() - mouseX * CLICK_GUI.get().guiScale.getValue()));
                    setLastPosY((float) (getPosY() - mouseY * CLICK_GUI.get().guiScale.getValue()));
                }
                break;
            case 1:
                if (hovered)
                    setExtended(!isExtended());
                break;
            default:
                break;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0 && isDragging()) setDragging(false);
        if (isExtended()) getComponents().forEach(component -> component.mouseReleased(mouseX, mouseY, mouseButton));
    }

    protected void drawStringWithShadow(DrawContext context, String text, double x, double y, int color) {
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, (int) x, (int) y, color);
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public String getLabel() {
        return label;
    }

    public float getWidth() {
        return width * CLICK_GUI.get().guiScale.getValue();
    }

    public float getHeight() {
        return height * CLICK_GUI.get().guiScale.getValue();
    }

    public float getPosX() {
        return posX * CLICK_GUI.get().guiScale.getValue();
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY * CLICK_GUI.get().guiScale.getValue();
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getLastPosX() {
        return lastPosX;
    }

    public void setLastPosX(float lastPosX) {
        this.lastPosX = lastPosX;
    }

    public float getLastPosY() {
        return lastPosY;
    }

    public void setLastPosY(float lastPosY) {
        this.lastPosY = lastPosY;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
