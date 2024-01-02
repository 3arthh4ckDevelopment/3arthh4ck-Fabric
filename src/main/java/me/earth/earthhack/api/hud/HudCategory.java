package me.earth.earthhack.api.hud;

import me.earth.earthhack.api.util.AbstractCategory;

public class HudCategory extends AbstractCategory {

    public static final HudCategory Visual = new HudCategory("Visual", 0);
    public static final HudCategory Text = new HudCategory("Text", 1);

    private static final HudCategory[] VALUES = {
        Visual, Text,
    };

    public static HudCategory[] values() {
        return VALUES.clone();
    }

    public HudCategory(String name, int ordinal) {
        super(name, ordinal);
    }
}
