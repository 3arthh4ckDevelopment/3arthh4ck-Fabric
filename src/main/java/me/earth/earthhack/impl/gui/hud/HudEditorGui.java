package me.earth.earthhack.impl.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.earthhack.impl.util.misc.GuiUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NonnullDefault;

import java.awt.*;
import java.util.List;
import java.util.*;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class HudEditorGui extends Screen
{

    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final Identifier BLACK_PNG =
            new Identifier("earthhack:textures/gui/black.png");

    private static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);
    private static final ModuleCache<HudEditor> HUD_EDITOR = Caches.getModule(HudEditor.class);

    public static Map<String, List<SnapPoint>> snapPoints;
    private final Set<HudElement> elements = new HashSet<>();
    private final ArrayList<HudCategoryFrame> frames = new ArrayList<>();
    private boolean oldVal = false;
    private double mouseClickedX;
    private double mouseClickedY;
    private double mouseReleasedX;
    private double mouseReleasedY;
    private boolean selecting;

    public HudEditorGui(Text title) {
        super(title);
    }

    public void init() {
        getFrames().clear();
        int x = 100;
        for (HudCategory hudCategory : HudCategory.values()) {
            getFrames().add(new HudCategoryFrame(hudCategory, Managers.ELEMENTS, x, 14, 110, 16));
            x += 130;
        }
        getFrames().forEach(Frame::init);

        oldVal = CLICK_GUI.get().catEars.getValue();
        snapPoints = new HashMap<>();
        List<SnapPoint> points = new ArrayList<>();
        Window resolution = MinecraftClient.getInstance().getWindow();
        points.add(new SnapPoint(2,resolution.getScaledHeight() - 4, 2, true, SnapPoint.Orientation.LEFT));
        points.add(new SnapPoint(2, resolution.getScaledHeight() - 4, resolution.getScaledWidth() - 2, true, SnapPoint.Orientation.RIGHT));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, 2, true, SnapPoint.Orientation.TOP));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, resolution.getScaledHeight() - 2, true, SnapPoint.Orientation.BOTTOM));
        points.add(new SnapPoint(2, resolution.getScaledHeight() - 4, resolution.getScaledWidth() / 2.0f, true, SnapPoint.Orientation.VERTICAL_CENTER));
        points.add(new SnapPoint(2, resolution.getScaledWidth() - 4, resolution.getScaledHeight() / 2.0f, true, SnapPoint.Orientation.HORIZONTAL_CENTER));
        snapPoints.put("default", points);
    }

    @Override
    @NonnullDefault
    public void resize(MinecraftClient mcIn, int w, int h) {
        super.resize(mcIn, w, h);
        init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (mc.world == null)
        {
            if (BACK.getValue())
            {
                this.renderBackground(context, mouseX, mouseY, delta);
            }
            else
            {
                RenderSystem.disableCull();
                // RenderSystem.disableFog();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                mc.getTextureManager().bindTexture(BLACK_PNG);
                RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
                bufferbuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferbuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).next();
                bufferbuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).next();
                bufferbuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, 0).color(64, 64, 64, 255).next();
                bufferbuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, 0).color(64, 64, 64, 255).next();
                GL11.glPushMatrix();
                tessellator.draw();
            }
        }

        if (oldVal != CLICK_GUI.get().catEars.getValue()) {
            init();
            oldVal = CLICK_GUI.get().catEars.getValue();
        }

        if (CLICK_GUI.get().blur.getValue() == ClickGui.BlurStyle.Directional) {
            Window scaledResolution = MinecraftClient.getInstance().getWindow();
            Render2DUtil.drawBlurryRect(context.getMatrices(), 0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), CLICK_GUI.get().blurAmount.getValue(),CLICK_GUI.get().blurSize.getValue());
        }

        for (List<SnapPoint> points : snapPoints.values()) {
            for (SnapPoint point : points) {
                if (point.isVisible()) {
                    point.draw(context);
                }
                point.update(Managers.ELEMENTS.getRegistered());
            }
        }

        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled()) {
                element.guiUpdate(context, mouseX, mouseY);
                element.guiDraw(context);
            }
        }

        if (selecting) {
            double minX = Math.min(mouseX, mouseClickedX);
            double minY = Math.min(mouseY, mouseClickedY);
            double maxX = Math.max(mouseX, mouseClickedX);
            double maxY = Math.max(mouseY, mouseClickedY);
            Render2DUtil.drawBorderedRect(context.getMatrices(), (float) minX, (float) minY, (float) maxX, (float) maxY, 0.2f, new Color(255, 255, 255, 90).getRGB(), new Color(255, 255, 255, 160).getRGB());
        }
        getFrames().forEach(frame -> frame.drawScreen(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean charTyped(char character, int keyCode) {
        getFrames().forEach(frame -> frame.charTyped(character,keyCode));
        return super.charTyped(character, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        getFrames().forEach(frame -> frame.keyPressed(keyCode));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        List<HudElement> clicked = new ArrayList<>();
        boolean isDragging = false;
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled() && GuiUtil.isHovered(element, mouseX, mouseY)) {
                clicked.add(element);
                if (element.isDragging())
                    isDragging = true;
            }
        }
        clicked.sort(Comparator.comparing(HudElement::getZ));

        boolean clickedFrame = false;
        for (HudCategoryFrame frame : getFrames()) {
            if (GuiUtil.isHovered(frame, mouseX, mouseY)) {
                clickedFrame = true;
                break;
            }
            for (Component component : frame.getComponents()) {
                if (GuiUtil.isHovered(component.getFinishedX(), component.getFinishedY(), component.getWidth(), component.getHeight(), mouseX, mouseY)) {
                    clickedFrame = true;
                    break;
                }
            }
        }

        if (!clickedFrame) {
            if (!clicked.isEmpty()) {
                clicked.get(0).guiMouseClicked(mouseX, mouseY, mouseButton);
            } else if (!isDragging) {
                selecting = true;
                mouseClickedX = mouseX;
                mouseClickedY = mouseY;
            }
        }
        getFrames().forEach(frame -> frame.mouseClicked(mouseX, mouseY, mouseButton));
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (selecting) {
            mouseReleasedX = mouseX;
            mouseReleasedY = mouseY;
        }

        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            if (element.isEnabled()) {
                element.guiMouseReleased(mouseX, mouseY, mouseButton);
                double minX = Math.min(mouseClickedX, mouseReleasedX);
                double minY = Math.min(mouseClickedY, mouseReleasedY);
                double maxWidth = Math.max(mouseClickedX, mouseReleasedX) - minX;
                double maxHeight = Math.max(mouseClickedY, mouseReleasedY) - minY;
                if (selecting && GuiUtil.isOverlapping(
                        new double[]{minX, minY, minX + maxWidth, minY + maxHeight},
                        new double[]{element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight()})) {
                    element.setDraggingX((float) (mouseX - element.getX()));
                    element.setDraggingY((float) (mouseY - element.getY()));
                    element.setDragging(true);
                }
            }
        }
        selecting = false;
        getFrames().forEach(frame -> frame.mouseReleased(mouseX, mouseY, mouseButton));
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
        getFrames().forEach(frame -> {
            for (Component comp : frame.getComponents()) {
                if (comp instanceof ModuleComponent moduleComponent) {
                    for (Component component : moduleComponent.getComponents()) {
                        if (component instanceof KeybindComponent keybindComponent) {
                            keybindComponent.setBinding(false);
                        }
                        if (component instanceof StringComponent stringComponent) {
                            stringComponent.setListening(false);
                        }
                    }
                }
            }
        });
        for (HudElement element : Managers.ELEMENTS.getRegistered()) {
            element.setDragging(false);
        }
        selecting = false;
        elements.clear();
        HUD_EDITOR.disable();
    }

    public void onGuiOpened() {
        getFrames().forEach(frame -> {
            for (Component comp : frame.getComponents()) {
                if (comp instanceof ModuleComponent moduleComponent) {
                    for (Component component : moduleComponent.getComponents()) {
                        if (component instanceof ColorComponent colorComponent) {
                            float[] hsb = Color.RGBtoHSB(colorComponent.getColorSetting().getRed(), colorComponent.getColorSetting().getGreen(), colorComponent.getColorSetting().getBlue(), null);
                            colorComponent.setHue(hsb[0]);
                            colorComponent.setSaturation(hsb[1]);
                            colorComponent.setBrightness(hsb[2]);
                            colorComponent.setAlpha(colorComponent.getColorSetting().getAlpha() / 255.f);
                        }
                    }
                }
            }
        });
    }

    private ArrayList<HudCategoryFrame> getFrames() {
        return frames;
    }

}
