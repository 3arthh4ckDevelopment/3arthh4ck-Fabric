package me.earth.earthhack.impl.modules.client.settings;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.client.SimpleData;

public class SettingsModule extends Module {
    public static final Setting<Complexity> COMPLEXITY =
            new EnumSetting<>("Complexity", Complexity.Beginner);

    public SettingsModule() {
        super("Settings", Category.Client);
        this.register(COMPLEXITY);
        SimpleData data = new SimpleData(this, "Configure how Settings work.");
        data.register(
                COMPLEXITY,
                """
                        - Beginner : These settings can be understood by everyone!
                        - Medium : Requires some knowledge of clients and CrystalPvP.
                        - Expert : Possibly requires knowledge of the clients' codebase and can cause serious issues when badly configured.
                        - Dev : Not recommended if you don't know what you're doing.""");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo() {
        return COMPLEXITY.getValue().toString();
    }

    public static boolean shouldDisplay(Setting<?> setting) {
        return COMPLEXITY.getValue().shouldDisplay(setting);
    }

}
