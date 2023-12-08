package me.earth.earthhack.impl.modules.client.clickgui;

import me.earth.earthhack.api.module.data.DefaultData;

public class ClickGuiData extends DefaultData<ClickGui> {
    public ClickGuiData(ClickGui module){
        super(module);
        register(module.guiScale, "Sets the scale for the ClickGUI. This kinda works, if needed you can do" +
                " +clickgui guiscale 1 and it's at the original setting");
        register(module.color, "Accent color for the ClickGUI.");
        register(module.catEars, "Draws very cute CatEars on the ClickGui.");
        register(module.blur, "Blurs the background of the ClickGui.\n" +
                "- Directional : Default blur.\n" +
                "- Gaussian : Renders a gaussian blur shader.\n" +
                "- None : Doesn't render anything.");
        register(module.blurAmount, "Blur strength for Blur - Directional.");
        register(module.blurSize, "Size of the blur, for Blur - Directional.");
        register(module.scrollSpeed, "How fast the ClickGUI is scrolled through.");
        register(module.moduleBox, "Draws a thin outline to a modules settings.");
        register(module.description, "Whether or not the Description box should be drawn.");
        register(module.showBind, "Shows modules' bindings next to their names in the ClickGUI" +
                " in brackets. For example: [B]");
        register(module.size, "Displays how many modules are in each category.");
        register(module.descriptionWidth, "The width the description box should be.");
        register(module.descNameValue, "When enabled, description boxes will show the type of setting," +
                " e.g. Boolean and its value, e.g. true/false.");
        register(module.modulesColorsetting, "The module colors.");
        register(module.moduleHoversetting, "The module hovering colors.");
        register(module.settingColorsetting, "The modules internal settings colors.");
        register(module.onModulesetting, "The color of an active module text.");
        register(module.offModulesetting, "The color of an inactive module text.");
        register(module.topColorsetting, "The section color.");
        register(module.topBgColorsetting, "The section background color.");
        register(module.textColorDescsetting, "Description text color.");
        register(module.catEarsSetting, "The CatEars color.");
    }
    public String getDescription() {
        return "Beautiful ClickGui by oHare.";
    }

    public String[] getAliases() {
        return new String[]{"Gui", "UI"};
    }

}
