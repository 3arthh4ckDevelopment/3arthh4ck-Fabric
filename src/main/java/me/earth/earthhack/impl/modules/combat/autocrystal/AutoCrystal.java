package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.modules.combat.autocrystal.helpers.PositionHistoryHelper;

public class AutoCrystal extends Module {

    public static final PositionHistoryHelper POSITION_HISTORY =
            new PositionHistoryHelper();

    public AutoCrystal() {
        super("AutoCrystal", Category.Combat);
    }
}
