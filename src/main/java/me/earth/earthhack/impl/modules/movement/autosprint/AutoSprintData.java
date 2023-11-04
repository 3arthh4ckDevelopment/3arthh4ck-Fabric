package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.data.DefaultData;

public class AutoSprintData extends DefaultData<AutoSprint> {
    public AutoSprintData(AutoSprint module) {
        super(module);
        register(module.mode, """
                Mode for sprinting.
                - Legit : Legitimate sprint, simulates Keys.
                - Rage : Obvious AutoSprint. Allows you to sprint into any direction.""");
    }
}
