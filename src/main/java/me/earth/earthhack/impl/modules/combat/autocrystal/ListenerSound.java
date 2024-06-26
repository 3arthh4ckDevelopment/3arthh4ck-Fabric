package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

final class ListenerSound extends SoundObserver
{
    private final AutoCrystal module;

    /**
     * Constructs a new SoundObserver.
     * Used by AutoCrystal for SoundThread and SoundRemove settings.
     * @param module an AutoCrystal instance.
     **/
    public ListenerSound(AutoCrystal module)
    {
        super(module.soundRemove::getValue);
        this.module = module;
    }

    @Override
    public void onChange(PlaySoundS2CPacket value)
    {
        // TODO: check that sound is in range!
        if (module.soundThread.getValue())
        {
            module.threadHelper.startThread();
        }
    }

    @Override
    public boolean shouldBeNotified()
    {
        return true;
    }

}
