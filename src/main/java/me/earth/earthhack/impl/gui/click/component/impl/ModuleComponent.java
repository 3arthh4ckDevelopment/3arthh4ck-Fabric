package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.configs.ConfigHelperModule;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModuleComponent extends Component {
    private final Module module;
    private final float height;
    private final ArrayList<Component> components = new ArrayList<>();

    public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getName(), posX, posY, offsetX, offsetY, width, height);
        this.height = height;
        this.module = module;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        ModuleData<?> data = getModule().getData();
        if (data != null) {
            this.setDescription(data::getDescription);
        }

        if (!getModule().getSettings().isEmpty()) {
            for (Setting<?> setting : getModule().getSettings()) {
                if (!setting.getVisibility()) continue;
                float before = offY;
                if (setting instanceof BooleanSetting && !setting.getName().equalsIgnoreCase("enabled")) {
                    getComponents().add(new BooleanComponent((BooleanSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof BindSetting) {
                    getComponents().add(new KeybindComponent((BindSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof NumberSetting) {
                    getComponents().add(new NumberComponent((NumberSetting<Number>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof EnumSetting) {
                    getComponents().add(new EnumComponent<>((EnumSetting<?>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ColorSetting) {
                    getComponents().add(new ColorComponent((ColorSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof StringSetting) {
                    getComponents().add(new StringComponent((StringSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
                if (setting instanceof ListSetting) {
                    getComponents().add(new ListComponent<>((ListSetting<?>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }

                // -_- lazy
                if (data != null && before != offY) {
                    Supplier<String> supplier = () -> {
                        String desc = data.settingDescriptions().get(setting);
                        if (desc == null) {
                            desc = "A Setting (" + setting.getInitial().getClass().getSimpleName() + ").";
                        }

                        if (Click.CLICK_GUI.get().descNameValue.getValue()) {
                            desc = ComponentFactory.create(setting).getText() + "\n\n" + TextColor.WHITE + desc;
                        }

                        return desc;
                    };

                    getComponents().get(getComponents().size() - 1).setDescription(supplier);
                }
            }
        }
        getComponents().forEach(Component::init);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
        getComponents().forEach(component -> component.moved(getFinishedX(), getFinishedY()));
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        if (!module.searchVisibility) {
            setHeight(0);
            if (isExtended())
                setExtended(false);
            return;
        } else {
            setHeight(height);
        }

        final boolean hovered = Render2DUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered)
            Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, getClickGui().get().getModuleHover().brighter().getRGB());
        if (getModule().isEnabled()) {
            Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, hovered ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB());
        }

        String label = module instanceof ConfigHelperModule && ((ConfigHelperModule) module).isDeleted() ? TextColor.RED + getLabel() : getLabel();
        drawStringWithShadow(label, getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getModule().isEnabled() ? getClickGui().get().getOnModule().brighter().getRGB() : getClickGui().get().getOffModule().brighter().getRGB());
        if (!getComponents().isEmpty())
            drawStringWithShadow(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue(), getFinishedX() + getWidth() - 4 - Managers.TEXT.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()), getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getModule().isEnabled() ? getClickGui().get().getOnModule().brighter().getRGB() : getClickGui().get().getOffModule().brighter().getRGB());

        if (getClickGui().get().showBind.getValue() && !getModule().getBind().toString().equalsIgnoreCase("none")) {
            String moduleBinding = getModule().getBind().toString().toLowerCase().replace("none", "-");
            moduleBinding = String.valueOf(moduleBinding.charAt(0)).toUpperCase() + moduleBinding.substring(1);
            if (moduleBinding.length() > 3) {
                moduleBinding = moduleBinding.substring(0, 3);
            }
            moduleBinding = "[" + moduleBinding + "]";
            float offset = getFinishedX() + getWidth() - Managers.TEXT.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()) * 2;
            Managers.TEXT.drawStringScaled(context, moduleBinding, offset - (Managers.TEXT.getStringWidth(moduleBinding) >> 1), (getFinishedY() + getHeight() / 1.5f - (Managers.TEXT.getStringHeightI() >> 1)), getModule().isEnabled() ? getClickGui().get().getOnModule().brighter().getRGB() : getClickGui().get().getOffModule().brighter().getRGB(), true, 0.5f);
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.drawScreen(context, mouseX, mouseY, partialTicks);
                }
            }
            if (getModule().isEnabled()) {
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB());
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB());
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB());
            }
            if (getClickGui().get().moduleBox.getValue() == ClickGui.ModuleBox.Old)
                Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() + getComponentsSize() + 0.5f, 0.5f, 0, 0xff000000);
            else if (getClickGui().get().moduleBox.getValue() == ClickGui.ModuleBox.New)
                Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 2.5f, getFinishedY() + getHeight() + getComponentsSize(), 0.5f, 0, 0xff000000);

        }
        if (getClickGui().get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + 1 + getWidth() - 2, getFinishedY() - 0.5f + getHeight() + (isExtended() ? (getComponentsSize() + 3.0f) : 0), 0.5f, 0, 0xff000000);

        updatePositions();
    }


    @Override
    public void charTyped(char character, int keyCode) {
        super.charTyped(character, keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.charTyped(character, keyCode);
                }
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.keyPressed(keyCode);
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = Render2DUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0 -> getModule().toggle();
                case 1 -> {
                    if (!getComponents().isEmpty())
                        setExtended(!isExtended());
                }
                default -> {
                }
            }
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    private float getComponentsSize() {
        float size = 0;
        for (Component component : getComponents()) {
            if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                size += component.getHeight();
            }
        }
        return size;
    }

    private void updatePositions() {
        float offsetY = getHeight();
        for (Component component : getComponents()) {
            if (component instanceof SettingComponent
                    && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                component.setOffsetY(offsetY);
                component.moved(getPosX(), getPosY());
                offsetY += component.getHeight();
            }
        }
    }

    public Module getModule() {
        return module;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
