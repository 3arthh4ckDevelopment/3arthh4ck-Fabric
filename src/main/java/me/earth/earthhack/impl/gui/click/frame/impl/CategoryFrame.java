package me.earth.earthhack.impl.gui.click.frame.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.gui.click.component.impl.ModuleComponent;
import me.earth.earthhack.impl.managers.client.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class CategoryFrame extends ModulesFrame {
    private final Category moduleCategory;
    private final ModuleManager moduleManager;

    public CategoryFrame(Category moduleCategory, ModuleManager moduleManager, float posX, float posY, float width, float height) {
        super(moduleCategory.getName(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
        this.moduleManager = moduleManager;
        this.setExtended(true);
    }

    @Override
    public void init() {
        getComponents().clear();
        float offsetY = getHeight() + 1;
        List<Module> moduleList = moduleManager.getModulesFromCategory(getModuleCategory());

        moduleList.sort(Comparator.comparing(Module::getName));
        for (Module module : moduleList) {
            if (module.isVisible()) {
                getComponents().add(new ModuleComponent(module, getPosX(), getPosY(), 0, offsetY, getWidth(), 14));
                offsetY += 14;
            }
        }
        super.init();
    }

    public Category getModuleCategory() {
        return moduleCategory;
    }

    private static final Identifier LEFT_EAR = new Identifier("earthhack:textures/gui/left_ear.png");
    private static final Identifier RIGHT_EAR = new Identifier("earthhack:textures/gui/right_ear.png");

    public static void catEarsRender(DrawContext context, float x, float y, float width) {
        final Color clr = Click.CLICK_GUI.get().getCatEars();
        GlStateManager._clearColor(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1.0F);
        context.drawTexture(LEFT_EAR, (int) x - 7, (int) y - 8, 0, 0, 20, 20, 20, 20, 20, 20); //TODO: check
        context.drawTexture(RIGHT_EAR, (int) (x + width) - 14, (int) y - 8, 0, 0, 20, 20, 20, 20, 20, 20);
        GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}