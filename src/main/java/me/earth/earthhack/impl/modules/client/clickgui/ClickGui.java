package me.earth.earthhack.impl.modules.client.clickgui;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.module.util.PluginsCategory;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.event.events.render.CrosshairEvent;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static me.earth.earthhack.impl.gui.click.Click.CLICK_GUI;

public class ClickGui extends Module
{

    private final Setting<Pages> pages =
            register(new EnumSetting<>("Page", Pages.General))
                    .setComplexity(Complexity.Medium);

    /* ---------------- General -------------- */
    public final Setting<Float> guiScale =
            register(new NumberSetting<>("Scale", 1.0f, 0.6f, 1.4f));
    public final Setting<Boolean> simpleColors =
            register(new BooleanSetting("Simple-Color", true));
    public final Setting<Color> color =
            register(new ColorSetting("Color", new Color(33, 85, 243, 255)));
    public final Setting<Integer> scrollSpeed =
            register(new NumberSetting<>("ScrollSpeed", 80, 1, 200));
    public final Setting<String> open =
            register(new StringSetting("Open", "-"));
    public final Setting<String> close =
            register(new StringSetting("Close", "+"));
    public final Setting<Boolean> showBind =
            register(new BooleanSetting("Show-Bind", true));
    public final Setting<Boolean> size =
            register(new BooleanSetting("Category-Size", false));

    /* ---------------- Blur -------------- */
    public final Setting<BlurStyle> blur =
            register(new EnumSetting<>("Blur", BlurStyle.Directional));
    public final Setting<Integer> blurAmount =
            register(new NumberSetting<>("Blur-Amount", 8, 1, 20));
    public final Setting<Integer> blurSize =
            register(new NumberSetting<>("Blur-Size", 3, 1, 20));

    /* ---------------- Modules -------------- */
    private final Setting<BoxesStyle> styleBoxes =
            register(new EnumSetting<>("Style-Boxes", BoxesStyle.New));
    public final Setting<ModuleBox> moduleBox =
            register(new EnumSetting<>("ModuleBox", ModuleBox.New));
    /* ---------------- Colors -------------- */
    protected Setting<Color> modulesColorsetting =
            register(new ColorSetting("ModulesColor", new Color(0, 80, 255, 255)));
    protected Setting<Color> moduleHoversetting =
            register(new ColorSetting("ModuleHover", new Color(0, 80, 255, 255)));
    protected Setting<Color> settingColorsetting =
            register(new ColorSetting("SettingColor", new Color(224, 224, 224, 255)));
    protected Setting<Color> onModulesetting =
            register(new ColorSetting("EnabledText", new Color(0, 80, 255, 255)));
    protected Setting<Color> offModulesetting =
            register(new ColorSetting("DisabledText", new Color(0, 80, 255, 255)));
    protected Setting<Color> topColorsetting =
            register(new ColorSetting("SectionColorSide", new Color(0, 80, 255, 255)));
    protected Setting<Color> topBgColorsetting =
            register(new ColorSetting("SectionBackGround", new Color(0, 0, 0, 255)));
    protected Setting<Color> textColorDescsetting =
            register(new ColorSetting("DescriptionColor", new Color(224, 224, 224, 255)));

    /* ---------------- Descriptions -------------- */
    public final Setting<Boolean> description =
            register(new BooleanSetting("Description", true));
    public final Setting<Integer> descriptionWidth =
            register(new NumberSetting<>("Description-Width", 240, 100, 210));
    public final Setting<Boolean> descNameValue =
            register(new BooleanSetting("Desc-NameValue", false));
    public final Setting<Float> descPosX =
            register(new NumberSetting<>("descPosY", 500.0f, 0.0f, 2000.0f))
                    .setVisibility(false);
    public final Setting<Float> descPosY =
            register(new NumberSetting<>("posX", 18.0f, 0.0f, 1000.0f))
                    .setVisibility(false);

    /* ---------------- Search -------------- */
    public final Setting<SearchStyle> search =
            register(new EnumSetting<>("Search", SearchStyle.None));
    public final Setting<Boolean> aliases =
            register(new BooleanSetting("Search-Aliases", true));
    public final Setting<Boolean> precision =
            register(new BooleanSetting("Search-Precise", true));
    public final Setting<Integer> searchWidth =
            register(new NumberSetting<>("Search-Width", 160, 100, 1000));
    public final Setting<Float> searchPosX =
            register(new NumberSetting<>("Search-X", 500.0f, 0.0f, 2000.0f))
                    .setVisibility(false);
    public final Setting<Float> searchPosY =
            register(new NumberSetting<>("Search-Y", 18.0f, 0.0f, 1000.0f))
                    .setVisibility(false);

    /* ---------------- CatEars -------------- */
    public final Setting<Boolean> catEars =
            register(new BooleanSetting("CatEars", false));
    protected Setting<Color> catEarsSetting =
            register(new ColorSetting("CatEars-Color", new Color(255, 0, 234, 255)));


    protected boolean fromEvent;
    protected Screen screen;

    public ClickGui()
    {
        super("ClickGui", Category.Client);
        setBind(Bind.fromKey(GLFW.GLFW_KEY_RIGHT_SHIFT));
        this.listeners.add(new ListenerScreen(this));
        this.setData(new ClickGuiData(this));

        new PageBuilder<>(this, pages)
                .addPage(p -> p == Pages.General, guiScale, size)
                .addPage(p -> p == Pages.Modules, styleBoxes, moduleBox)
                .addPage(p -> p == Pages.Blur, blur, blurSize)
                .addPage(p -> p == Pages.Colors, modulesColorsetting, textColorDescsetting)
                .addPage(p -> p == Pages.Description, description, descNameValue)
                .addPage(p -> p == Pages.Search, search, searchWidth)
                .addPage(p -> p == Pages.CatEars, catEars, catEarsSetting)
                .register(Visibilities.VISIBILITY_MANAGER);

        this.listeners.add(new EventListener<>(CrosshairEvent.class)
        {
            @Override
            public void invoke(CrosshairEvent event)
            {
                event.setCancelled(true);
            }
        });

    }

    public ClickGui(String name) // for PB-Gui and Config-Gui
    {
        super(name, Category.Client);
        this.listeners.add(new ListenerScreen(this));
        this.setData(new ClickGuiData(this));
        search.setVisibility(false);
        aliases.setVisibility(false);
        precision.setVisibility(false);
        searchWidth.setVisibility(false);
        new PageBuilder<>(this, pages)
                .addPage(p -> p == Pages.General, guiScale, size)
                .addPage(p -> p == Pages.Modules, styleBoxes, moduleBox)
                .addPage(p -> p == Pages.Blur, blur, blurSize)
                .addPage(p -> p == Pages.Colors, modulesColorsetting, textColorDescsetting)
                .addPage(p -> p == Pages.Description, description, descNameValue)
                .addPage(p -> p == Pages.CatEars, catEars, catEarsSetting)
                .register(Visibilities.VISIBILITY_MANAGER);
    }

    @Override
    protected void onEnable()
    {
        ChatUtil.sendMessage("faggtot");
        disableOtherGuis();
        Click.CLICK_GUI.set(this);
        screen = mc.currentScreen instanceof Click ? ((Click) mc.currentScreen).screen : mc.currentScreen;
        // don't save it since some modules add/del settings
        Click gui = newClick();
        if (Caches.getModule(Management.class).get().pluginSection.getValue())
            gui.setCategories(PluginsCategory.getInstance().getCategories());
        gui.init();
        gui.onGuiOpened();
        mc.setScreen(gui);

        /*
        if (blur.getValue() == BlurStyle.Gaussian && OpenGlHelper.shadersSupported)
                mc.entityRenderer(new Identifier("minecraft", "shaders/post/blur.json"));

         */

    }

    protected void disableOtherGuis() {
        for (Module module : Managers.MODULES.getRegistered()) {
            if (module instanceof ClickGui && module != this) {
                module.disable();
            }
        }
    }

    protected Click newClick() {
        return new Click(screen);
    }

    @Override
    protected void onDisable()
    {
        if (!fromEvent)
        {
            mc.setScreen(screen);
        }

        /*
        if (OpenGlHelper.shadersSupported)
            mc.entityRenderer.stopUseShader();
         */

        fromEvent = false;
    }

    public boolean getBoxes() {
        return styleBoxes.getValue() == BoxesStyle.Old;
    }

    /* COLORS */

    Color modulesColor = modulesColorsetting.getValue();
    Color moduleHover = moduleHoversetting.getValue();
    Color settingColor = settingColorsetting.getValue();
    Color onModule = onModulesetting.getValue();
    Color offModule = offModulesetting.getValue();
    Color topColor = topColorsetting.getValue();
    Color topBgColor = topBgColorsetting.getValue();
    Color textColorDesc = textColorDescsetting.getValue();
    Color catEarsV = catEarsSetting.getValue();

    public void updateColors() {
        if (simpleColors.getValue()) {
            modulesColor = CLICK_GUI.get().color.getValue();
            moduleHover = new Color(70,70,70,70);
            onModule = new Color(220, 220, 220,255);
            offModule = new Color(120, 120, 120,255);
            topColor = new Color(0,0,0,255);
            topBgColor = CLICK_GUI.get().color.getValue();
            textColorDesc = new Color(220, 220, 220,255);
            catEarsV = CLICK_GUI.get().color.getValue();
        } else {
            modulesColor = modulesColorsetting.getValue();
            moduleHover = moduleHoversetting.getValue();
            onModule = onModulesetting.getValue();
            offModule = offModulesetting.getValue();
            topColor = topColorsetting.getValue();
            topBgColor = topBgColorsetting.getValue();
            textColorDesc = textColorDescsetting.getValue();
            catEarsV = catEarsSetting.getValue();
        }
    }

    public Color getModulesColor() {
        updateColors();
        return modulesColor;
    }

    public Color getModuleHover() {
        updateColors();
        return moduleHover;
    }

    public Color getSettingColor() {
        updateColors();
        return settingColor;
    }

    public Color getOnModule() {
        updateColors();
        return onModule;
    }

    public Color getOffModule() {
        updateColors();
        return offModule;
    }

    public Color getTopColor() {
        updateColors();
        return topColor;
    }

    public Color getTopBgColor() {
        updateColors();
        return topBgColor;
    }

    public Color getTextColorDesc() {
        updateColors();
        return textColorDesc;
    }

    public Color getCatEars() {
        updateColors();
        return catEarsV;
    }


    /* ENUMS */

    private enum Pages {
        General,
        Modules,
        Blur,
        Colors,
        Description,
        Search,
        CatEars
    }

    private enum BoxesStyle {
        Old,
        New
    }

    public enum ModuleBox {
        None,
        New,
        Old
    }

    public enum BlurStyle {
        None,
        Directional,
        Gaussian
    }

    public enum SearchStyle {
        None,
        Box,
        TextBar
    }

}
