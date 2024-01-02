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
                this.renderBackground(context);
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
                double minX = Math.min(mouseClickedX, mouseX);
                double minY = Math.min(mouseClickedY, mouseY);
                double maxWidth = Math.max(mouseClickedX, mouseY) - minX;
                double maxHeight = Math.max(mouseClickedY, mouseY) - minY;
                if (GuiUtil.isOverlapping(
                        new double[]{minX, minY, minX + maxWidth, minY + maxHeight},
                        new double[]{element.getX(), element.getY(), element.getX() + element.getWidth(), element.getY() + element.getHeight()}))
                {
                    elements.add(element);
                }
                element.guiUpdate(mouseX, mouseY, delta);
                element.guiDraw(mouseX, mouseY, delta);
            }
        }

        if (selecting) {
            double minX = Math.min(mouseClickedX, mouseX);
            double minY = Math.min(mouseClickedY, mouseY);
            double maxWidth = Math.max(mouseClickedX, mouseY);
            double maxHeight = Math.max(mouseClickedY, mouseY);
            Render2DUtil.drawRect(context.getMatrices(), (float) minX, (float) minY, (float) maxWidth, (float) maxHeight, new Color(255, 255, 255, 128).getRGB());
        }
        getFrames().forEach(frame -> frame.drawScreen(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean charTyped(char character, int keyCode) {

        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiKeyPressed(character, keyCode);
            }
        }
        getFrames().forEach(frame -> frame.keyTyped(character,keyCode));
        return super.charTyped(character, keyCode);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        List<HudElement> clicked = new ArrayList<>();
        boolean hasDragging = false;
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled() && GuiUtil.isHovered(element, mouseX, mouseY)) {
                clicked.add(element);
                if (element.isDragging()) hasDragging = true;
                // element.guiMouseClicked(mouseX, mouseY, mouseButton);
            }
        }
        clicked.sort(Comparator.comparing(HudElement::getZ));

        if (!clicked.isEmpty()) {
            clicked.get(0).guiMouseClicked(mouseX, mouseY, mouseButton);
        }
        //TODO: fix this when we want to
        /* else {
            if (!GuiUtil.isHovered(frame, mouseX, mouseY) && !hasDragging) {
                selecting = true;
                mouseClickedX = mouseX;
                mouseClickedY = mouseY;
                return;
            }
        }
         */
        getFrames().forEach(frame -> frame.mouseClicked(mouseX,mouseY,mouseButton));
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (selecting) {
            mouseReleasedX = mouseX;
            mouseReleasedY = mouseY;
        }
        for (HudElement element : Managers.ELEMENTS.getRegistered())
        {
            if (element.isEnabled()) {
                element.guiMouseReleased(mouseX, mouseY, mouseButton);
                if (elements.remove(element) && selecting) {
                    element.setDraggingX((float) mouseX - element.getX());
                    element.setDraggingY((float) mouseY - element.getY());
                    element.setDragging(true); // TODO: better solution
                }
            }
        }
        selecting = false;
        getFrames().forEach(frame -> frame.mouseReleased(mouseX,mouseY,mouseButton));
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
