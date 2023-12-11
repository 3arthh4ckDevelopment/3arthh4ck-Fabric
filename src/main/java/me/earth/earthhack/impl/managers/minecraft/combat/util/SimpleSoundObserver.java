package me.earth.earthhack.impl.managers.minecraft.combat.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class SimpleSoundObserver extends SoundObserver
{
    public SimpleSoundObserver()
    {
        this(() -> true);
    }

    public SimpleSoundObserver(BooleanSupplier soundRemove)
    {
        super(soundRemove);
    }

    @Override
    public void onChange(PlaySoundS2CPacket value)
    {
        /* Nothing */
    }

}
