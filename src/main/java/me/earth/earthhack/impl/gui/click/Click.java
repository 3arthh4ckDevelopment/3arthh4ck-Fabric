package me.earth.earthhack.impl.gui.click;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.impl.ColorComponent;
import me.earth.earthhack.impl.gui.click.component.impl.KeybindComponent;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.gui.click.component.impl.StringComponent;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.gui.click.frame.impl.CategoryFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.DescriptionFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.ModulesFrame;
import me.earth.earthhack.impl.gui.click.frame.impl.SearchFrame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.pingbypass.modules.SyncModule;
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
import java.util.ArrayList;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class Click extends Screen {
    public static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final Identifier BLACK_PNG =
            new Identifier("earthhack:textures/gui/black.png");
    private final ArrayList<Frame> frames = new ArrayList<>();
    private Category[] categories = Category.values();
    private final ModuleManager moduleManager;
    private boolean oldVal = false;
    private boolean attached = false;
    private boolean addDescriptionFrame = true;
    private boolean pingBypass;

    public final Screen screen;

    public static DescriptionFrame descriptionFrame =
            new DescriptionFrame(0, 0, 200, 18); // moved this here, so it's possible to use all the variables above in the future

    public Click(Screen screen) {
        super(Text.of("ClickGui"));
        this.moduleManager = Managers.MODULES;
        this.screen = screen;
    }

    public Click(Screen screen, ModuleManager moduleManager) {
        super(Text.of("ClickGui"));
        this.moduleManager = moduleManager;
        this.screen = screen;
    }

    public void init() {
        if (!attached) {
            CLICK_GUI.get().descriptionWidth.addObserver(e -> descriptionFrame.setWidth(e.getValue()));
            attached = true;
        }

        getFrames().clear();
        int x = CLICK_GUI.get().catEars.getValue() ? 14 : 2;
        int y = CLICK_GUI.get().catEars.getValue() ? 14 : 2;
        for (Category moduleCategory : categories) {
            getFrames().add(new CategoryFrame(moduleCategory, moduleManager, x, y, 110, 16));
            if (x + 220 >= MinecraftClient.getInstance().getWindow().getScaledWidth()) {
                x = CLICK_GUI.get().catEars.getValue() ? 14 * Math.round(CLICK_GUI.get().guiScale.getValue()) : 2;
                y += CLICK_GUI.get().catEars.getValue() ? 32 * CLICK_GUI.get().guiScale.getValue() : 20;
            } else
                x += (CLICK_GUI.get().catEars.getValue() ? 132 * CLICK_GUI.get().guiScale.getValue() : 112);
        }

        if (addDescriptionFrame) {
            descriptionFrame = new DescriptionFrame(CLICK_GUI.get().descPosX.getValue(), CLICK_GUI.get().descPosY.getValue(), CLICK_GUI.get().descriptionWidth.getValue(), 16);
            getFrames().add(descriptionFrame);
        }

        if (pingBypass) {
            DescriptionFrame hint = new DescriptionFrame("Info", x, y + 100, CLICK_GUI.get().descriptionWidth.getValue(), 16);
            hint.setDescription("You are editing the modules running on the PingBypass server, not the ones which run here on your client.");
            getFrames().add(hint);

            ModulesFrame pbFrame = new ModulesFrame("PingBypass", x, y + 200, 110, 16);
            pbFrame.getComponents().add(new ModuleComponent(new SyncModule(), pbFrame.getPosX(), pbFrame.getPosY(), 0, pbFrame.getHeight() + 1, pbFrame.getWidth(), 14));
            getFrames().add(pbFrame);
        }

        if (CLICK_GUI.get().search.getValue() != ClickGui.SearchStyle.None) {
            SearchFrame searchFrame = new SearchFrame();
            getFrames().add(searchFrame);
            searchFrame.clearInput();
        }

        getFrames().forEach(Frame::init);
        oldVal = CLICK_GUI.get().catEars.getValue();
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
            final Window scaledResolution = MinecraftClient.getInstance().getWindow();
            // Render2DUtil.drawBlurryRect(context.getMatrices() , 0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), CLICK_GUI.get().blurAmount.getValue(), CLICK_GUI.get().blurSize.getValue());
        }

        getFrames().forEach(frame -> frame.drawScreen(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        getFrames().forEach(frame -> frame.keyTyped(chr, modifiers));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        getFrames().forEach(frame -> frame.mouseClicked(mouseX,mouseY,mouseButton));
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
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

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public void setPingBypass(boolean pingBypass) {
        this.pingBypass = pingBypass;
    }

    public void setAddDescriptionFrame(boolean addDescriptionFrame) {
        this.addDescriptionFrame = addDescriptionFrame;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

}
