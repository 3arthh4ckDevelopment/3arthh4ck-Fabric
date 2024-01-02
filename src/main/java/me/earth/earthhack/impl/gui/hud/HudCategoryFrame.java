package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.managers.client.HudElementManager;

import java.util.Comparator;
import java.util.List;

public class HudCategoryFrame extends HudFrame {
    private final HudCategory moduleCategory;
    private final HudElementManager moduleManager;

    public HudCategoryFrame(HudCategory moduleCategory, HudElementManager moduleManager, float posX, float posY, float width, float height) {
        super(moduleCategory.getName(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
        this.moduleManager = moduleManager;
        this.setExtended(true);
    }

    @Override
    public void init() {
        getComponents().clear();
        float offsetY = getHeight() + 1;
        List<HudElement> moduleList = moduleManager.getModulesFromCategory(getModuleCategory());

        moduleList.sort(Comparator.comparing(HudElement::getName));
        for (HudElement element : moduleList) {
            getComponents().add(new HudElementComponent(element, getPosX(), getPosY(), 0, offsetY, getWidth(), 14));
            offsetY += 14;
        }
        super.init();
    }

    public HudCategory getModuleCategory() {
        return moduleCategory;
    }
}