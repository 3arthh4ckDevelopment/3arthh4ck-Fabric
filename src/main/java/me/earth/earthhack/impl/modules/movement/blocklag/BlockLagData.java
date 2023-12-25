package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.module.data.DefaultData;

final class BlockLagData extends DefaultData<BlockLag>
{
    public BlockLagData(BlockLag module)
    {
        super(module);
        register(module.offsetMode, "Mode for offsets BlockLag should use.\n" +
                "- Constant : \n" +
                "- Smart : \n" +
                "- Motion : Chokes packets and increases your Motion." +
                "- SmartNew : Made by xyzbtw, especially useful for 9b9t and 0b0t. Configurable " +
                "from page Smart.");

        register(module.vClip,
                "V-clips the specified amount down to cause a lagback." +
                        " Don't touch, 9 should be perfect.\n" +
                        "For modes Constant and Smart.");
        register(module.minDown, "For OffsetMode - Smart: Minimum down ");


        register(module.timerAmount, "What the timer should be set to.");
        register(module.motionAmount, "Amount of motion the player should get.");
        register(module.useTimer, "Use timer when attempting lagback to" +
                        " make it easier to accomplish.");
        register(module.useBlink, "New revolutionary bypass for Crystalpvp.cc." +
                " Temporarily disables sending any CPacketPlayer packets, so" +
                " you can burrow in this time. In development, but functional!");
        register(module.blinkDuration, "How long Blink should be used for. For cc," +
                " this is pretty good and stable at around 100-500ms dependant on Timer." +
                " Not having this high enough can result in failure." +
                " Having it too high can result in disconnecting, or" +
                " failure.");
        register(module.autoDisableBlink, "Automatically disables blink.");
        register(module.motionNegate, "Negates motion after some time to potentially speed up burrowing and" +
                " make it more reliable.");
        register(module.negateAmount, "Amount of motion to counteract the original motion." +
                " Typically this should be equal to the original motion.");

    }


    @Override
    public String getDescription()
    {
        return "The OG Burrow.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"Burrow", "SelfFill"};
    }

}