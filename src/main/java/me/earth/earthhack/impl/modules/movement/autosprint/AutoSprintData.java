package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.data.DefaultData;

public class AutoSprintData extends DefaultData<AutoSprint> {
    public AutoSprintData(AutoSprint module) {
        super(module);
        register(module.mode, """
                Mode for sprinting.
                - Legit : Legitimate sprint, simulates Keys.
                - Rage : Obvious AutoSprint. Allows you to sprint into any direction.""");
        register(module.faceDirection, "Makes you rotate towards the direction " +
                "you're moving in. Only for AntiCheats that check this, like Grim.");
    }

    public String getDescription() {
        return "Automatically sprints.";
    }
}
