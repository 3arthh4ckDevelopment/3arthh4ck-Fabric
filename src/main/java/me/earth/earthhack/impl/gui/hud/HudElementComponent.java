package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.module.data.ModuleData;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.gui.click.component.Component;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.gui.click.component.impl.*;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.function.Supplier;

// TODO: Again, maybe generify these classes in the future. Making HudElement a subclass of Module is messy.
public class HudElementComponent extends Component {

    private static final SettingCache<Boolean, BooleanSetting, ClickGui> WHITE =
            Caches.getSetting(ClickGui.class, BooleanSetting.class, "White-Settings", true);

    private final HudElement element;
    private final ArrayList<Component> components = new ArrayList<>();

    public HudElementComponent(HudElement element, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(element.getName(), posX, posY, offsetX, offsetY, width, height);
        this.element = element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        ModuleData<?> data = getElement().getData();
        if (data != null) {
            this.setDescription(data::getDescription);
        }

        if (!getElement().getSettings().isEmpty()) {
            for (Setting<?> setting : getElement().getSettings()) {
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
                if (data != null && before != offY)  {
                    Supplier<String> supplier = () -> {
                        String desc = data.settingDescriptions().get(setting);
                        if (desc == null) {
                            desc = "A Setting (" + setting.getInitial().getClass().getSimpleName() + ").";
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
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());

        if (hovered)
            Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, 0x66333333);
        if (getElement().isEnabled()) {
            Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
        }
        Managers.TEXT.drawStringWithShadow(getLabel(), getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getElement().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (!getComponents().isEmpty())
            Managers.TEXT.drawStringWithShadow(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue(), getFinishedX() + getWidth() - 4 - Managers.TEXT.getStringWidth(isExtended() ? getClickGui().get().close.getValue() : getClickGui().get().open.getValue()), getFinishedY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1), getElement().isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.drawScreen(context, mouseX, mouseY, partialTicks);
                }
            }
            if (getElement().isEnabled()) {
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
                Render2DUtil.drawRect(context.getMatrices(), getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? getClickGui().get().color.getValue().brighter().getRGB() : getClickGui().get().color.getValue().getRGB());
            }
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 3.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() + getComponentsSize() + 0.5f, 0.5f, 0, WHITE.getValue() ? 0xffffffff :  0xff000000);

        }
        Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + 1 + getWidth() - 2, getFinishedY() - 0.5f + getHeight() + (isExtended() ? (getComponentsSize() + 3.0f) : 0), 0.5f, 0, 0xff000000);
        updatePositions();
    }


    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof SettingComponent
                        && Visibilities.VISIBILITY_MANAGER.isVisible(((SettingComponent<?, ?>) component).getSetting())) {
                    component.keyTyped(character, keyCode);
                }
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0:
                    getElement().toggle();
                    break;
                case 1:
                    if (!getComponents().isEmpty())
                        setExtended(!isExtended());
                    break;
                default:
                    break;
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

    public ArrayList<Component> getComponents() {
        return components;
    }

    public HudElement getElement() {
        return element;
    }
}
