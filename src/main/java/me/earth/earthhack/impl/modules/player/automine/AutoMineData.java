package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoMineData extends DefaultData<AutoMine>
{
    public AutoMineData(AutoMine module)
    {
        super(module);
        register(module.mode, """
                - Combat : Will strategically mine enemies out. Uses Speedmine - Smart.
                - AntiTrap : Will mine you out of traps.
                - Compatibility : Makes this module compatible with other clients' Speedmines.""");
        register(module.range, "Range in which blocks will be mined.");
        register(module.head, "Mines the Block above the Target.");
        register(module.rotate, "Rotates to mine the block.");
        register(module.rotateLimit, "For Mode - Compatibility: Waits" +
                " this Setting's amount of time in milliseconds before" +
                " rotating. Might be useful if you're having trouble breaking" +
                " blocks.");
        register(module.maxY, "Most practice servers don't allow you to mine" +
                " blocks over a certain Y level. This settings makes AutoMine" +
                " not target positions above a certain Y level, so you can fix" +
                " it targeting blocks it can't actually mine.");
        register(module.self, "Touches Blocks in your own Surround so it can" +
                " be mined quickly if an enemy jumps in.");
        register(module.prioSelf, "Prioritizes untrapping yourself.");
        register(module.constellationCheck, "Dev Setting, should be on.");
        register(module.delay, "Delay between touching blocks.");
        register(module.newV, "Takes 1.13+ crystal mechanics into account.");
        register(module.checkCurrent, "Dev Setting, should be on.");
        register(module.improve, "Will actively search for a better position.");
        register(module.mineL, "For Combat: Mines out L-Shaped Holes");
        register(module.offset, "Time to wait after a block has been destroyed.");
        register(module.shouldBlackList,
                "Blacklists blocks that you reset by touching them again.");
        register(module.blackListFor, "Time in seconds a block should be " +
                "blacklisted for. A value of 0 means it will never be" +
                " blacklisted.");
        register(module.checkTrace, "Checks PlaceRange, PlaceTrace and" +
                " BreakTrace for the crystal position.");
        register(module.placeRange, "PlaceRange of your CA.");
        register(module.placeTrace, "PlaceTrace of your CA.");
        register(module.breakTrace, "BreakTrace of your CA.");
        register(module.selfEchestMine, "Will mine an Echest you burrowed with.");
        register(module.resetIfNotValid, "Doesn't keep invalid positions mined.");
        register(module.mineBurrow, "Will mine players burrow blocks.");
        register(module.checkPlayerState, "Checks if a player burrowed in the meantime.");
        register(module.antiStuckComp, "For Mode: Compatibility: " +
                "Resets the position AutoMine is mining if you click blocks" +
                " nearby. (e.g. try to mine something else).");
    }

    @Override
    public String getDescription()
    {
        return "Automatically mines Blocks.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"AutoCity"};
    }

}
