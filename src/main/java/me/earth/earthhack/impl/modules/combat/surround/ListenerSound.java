package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

final class ListenerSound extends SoundObserver
{
    private final Surround module;

    public ListenerSound(Surround module)
    {
        super(() -> module.shouldInstant(true));
        this.module = module;
    }

    @Override
    public void onChange(PlaySoundS2CPacket value)
    {
        ListenerMotion.start(module);
    }

}
