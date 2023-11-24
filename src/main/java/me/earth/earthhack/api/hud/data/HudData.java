package me.earth.earthhack.api.hud.data;

import me.earth.earthhack.api.config.preset.ModulePreset;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;

import java.util.Collection;
import java.util.Map;

/**
 * Container for a HUD elements' data:
 * - the color of the module.
 * - a description of the module.
 * - descriptions for each setting of the module.
 * - a collection of presets for the module.
 */
public interface HudData<M extends SettingContainer>
{

    /**
     * Returns the element's description.
     * Hopefully detailed!
     *
     * @return a description of the element.
     */
    String getDescription();

    /**
     * Returns Settings belonging to this element
     * mapped to a description. The exact Description map
     * will be returned, meaning that putting in and removing is
     * supported.
     *
     * @return settings with their descriptions.
     */
    Map<Setting<?>, String> settingDescriptions();

    /**
     * Returns a collection of the modules presets.
     * The exact Collection will be returned, meaning adding
     * and removing presets is supported.
     *
     * @return the element's presets.
     */
    Collection<ModulePreset<M>> getPresets();

}
