package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.impl.util.helpers.blocks.data.ObbyData;

final class AntiTrapData extends ObbyData<AntiTrap>
{
    public AntiTrapData(AntiTrap module)
    {
        super(module);
        register(module.mode, """
                - Crystal : Places crystals in nearby spots to prevent AutoTraps.
                - Faceplace : Places obsidian to fill your surround higher to prevent faceplacing.
                - Fill : Places obsidian in holes next to your surround to make surround failure harder.
                - Bomb : Basically extended SelfTrap, prevents you from getting CrystalBombed.""");
        register(module.offhand, "Switches to the Offhand.");
        register(module.waitForMine, "If you want to place blocks only after someone " +
                "started mining you out.");
        register(module.timeOut, "Interval between toggling this module." +
                " (for fat fingers)");
        register(module.empty, "For Mode-Crystal: Disable the module if no " +
                "suitable position can be found. Otherwise it will run until " +
                "it can place a crystal.");
        register(module.mineRange, "When using Smart blocks within this range will " +
                "be considered your own.");
        register(module.mineTime, "The delay for blocks to be placed after they have " +
                "been hit.");
        register(module.swing, "If you want to see your hand swinging or not.");
        register(module.highFill, "For Mode-Faceplace/Fill: If all feettrap" +
                " positions should be filled 2 blocks high.");
        register(module.confirm,
                "Time for the server to confirm Blockplacements.");
    }

    @Override
    public String getDescription()
    {
        return "Places crystals or obsidian to prevent AutoTraps.";
    }
}
