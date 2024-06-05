package me.earth.earthhack.impl.modules.misc.settingspoof;

import net.minecraft.util.Arm;

public enum HandTranslator
{
    Left(Arm.LEFT),
    Right(Arm.RIGHT);

    private final Arm handSide;

    HandTranslator(Arm visibility)
    {
        this.handSide = visibility;
    }

    public Arm getHandSide()
    {
        return handSide;
    }
}
