package me.earth.earthhack.impl.modules.player.swing;

import me.earth.earthhack.api.module.data.DefaultData;

final class SwingData extends DefaultData<Swing> {
    public SwingData(Swing module) {
        super(module);
        register(module.swingSpeed, "The delay between your hand swings. 6 is default speed." +
                " The higher the speed is, longer it will take to swing.");
        register(module.clientside, "Makes Swing client-sided.");
        register(module.mode, "What kind of swinging modifier should be applied.\n" +
                "\nMainhand - Only swing with your mainhand" +
                "\nOffhand - Only swing with your offhand" +
                "\nPacket - Doesn't matter which hand you swing with." +
                "\nSwitch - Switches up which hand you swing with." +
                " For example, actually swinging your offhand" +
                " will swing your Mainhand instead, and vice-versa.. In development!!!!" +
                "\nShuffle - Shuffles the hands you swing with. In development!!!!");
    }

    @Override
    public int getColor() {
        return 0xffffffff;
    }

    @Override
    public String getDescription() {
        return "Change your hand swing speed.";
    }
}
