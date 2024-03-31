package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.module.data.DefaultData;

final class SpeedMineData extends DefaultData<Speedmine>
{
    public SpeedMineData(Speedmine module)
    {
        super(module);
        register(module.mode, """
                - Reset : Nothing special, blocks won't be reset after you stopped mining them.
                - Packet : Touch a block to mine it later.
                - Smart : Much better packet. Allows you to decide that you want to go for another block in difference to Mode Packet.
                - Fast : Similar to Smart but makes mining the same position over and over faster.
                - Damage : Normal Mining but the the block will be mined instantly after the damage specified by the Damage Setting is reached.""");
        register(module.noReset, "Doesn't reset blocks after mining them.");
        register(module.resetSwap, "Resets the position if you swap hotbar " +
                "items. Needed for most servers.");
        register(module.limit, "After this amount of damage has been dealt" +
                " to the block it will be mined.");
        register(module.range, "If the currently mined block is outside " +
                "this range the mining process will be cancelled.");
        register(module.multiTask, "If you want to allow multitasking or not.");
        register(module.rotate, "Some servers (2b2tpvp) patched Packet" +
                " Speedmines by requiring you to look at the block." +
                " This will silently look at the block.");
        register(module.event, "Development Setting for compatibility with" +
                " BlockTweaks-NoMiningAnimation.");
        register(module.display, "Displays the damage dealt to the" +
                " block in the HUDs-Arraylist.");
        register(module.delay, "Delay between clicking a block. Good " +
                "for Mode-Smart which will stop mining the block when" +
                " you touch it again, so with this setting you cant" +
                " accidentally touch it twice.");
        register(module.esp, "Renders a Block-ESP around the " +
                "currently mined block.");
        register(module.onGround, "Normally you mine slower when you are" +
                " not standing on the ground. Most servers don't check" +
                " this tho, so this setting can be off.");
        register(module.placeCrystal, "Places a crystal when the block" +
                " is about to break if the block being broken is" +
                " placed at the feet.");
        register(module.toAir, "Attempts to set the block mined to air" +
                " client-sided instead of waiting for the response" +
                " from the server. Can cause Ghost blocks. Also, its" +
                " recommended to set the Limit Setting to 1 when" +
                " using this with Mode Smart or Packet.");
        register(module.normal, "Ignore code that 3arth doesn't understand." +
            " Recommended for Mode - Smart and maybe Fast," +
            " mind ResetAfterPacket, tho.");
        register(module.confirm,
                "Time for the Server to confirm that we broke the block.");
        register(module.requireBreakSlot, "Requires damage to be >= limit when using BreakBind");
        register(module.resetAfterPacket,
                 "Only for Mode - Smart with Normal on.");
        register(module.tpsSync, "Syncs mode Smart and Fast with the TPS.");
    }

    @Override
    public String getDescription()
    {
        return "All kinds of tweaks around mining blocks.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"PacketMine", "InstantMine"};
    }

}
