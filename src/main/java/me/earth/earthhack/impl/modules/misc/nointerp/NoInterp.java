package me.earth.earthhack.impl.modules.misc.nointerp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;

public class NoInterp extends Module {

    private final Setting<Boolean> silent =
            register(new BooleanSetting("Silent", true));
    private final Setting<Boolean> setRotations =
            register(new BooleanSetting("Fast-Rotations", false));
    private final Setting<Boolean> noDeathJitter =
            register(new BooleanSetting("NoDeathJitter", true));
    // TODO: THIS, problem with Jockeys... (fix now)
    private final Setting<Boolean> onlyPlayers =
            register(new BooleanSetting("OnlyPlayers", false));

    public NoInterp() {
        super("NoInterp", Category.Misc);
    }

    public boolean isSilent()
    {
        return silent.getValue();
    }

    public boolean shouldFixDeathJitter()
    {
        return noDeathJitter.getValue();
    }
    public boolean isOnlyPlayers() { return onlyPlayers.getValue(); }

    // todo rest
}
