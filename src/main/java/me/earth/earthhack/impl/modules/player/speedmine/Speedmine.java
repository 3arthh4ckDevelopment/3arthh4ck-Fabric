package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;

public class Speedmine extends Module {

    protected final Setting<MineMode> mode     =
            register(new EnumSetting<>("Mode", MineMode.Smart));

    public Speedmine() {
        super("SpeedMine", Category.Player);
    }

    public MineMode getMode()
    {
        return mode.getValue();
    }

    public int getOutlineAlpha() {
        return 1;
    }

    public int getBlockAlpha() {
        return 1;
    }
}
