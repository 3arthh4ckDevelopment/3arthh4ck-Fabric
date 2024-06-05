package me.earth.earthhack.impl.modules.misc.nosoundlag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;

final class ListenerSound extends
        ModuleListener<NoSoundLag, PacketEvent.Receive<PlaySoundS2CPacket>>
{
    public ListenerSound(NoSoundLag module)
    {
        super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlaySoundS2CPacket> event)
    {
        SoundEvent sound = event.getPacket().getSound().value();
        if ((module.armor.getValue() && NoSoundLag.ARMOR_SOUNDS.contains(sound))
                || (module.withers.getValue() && NoSoundLag.WITHER_SOUNDS.contains(sound)))
        {
            event.setCancelled(true);
        }
    }

}
