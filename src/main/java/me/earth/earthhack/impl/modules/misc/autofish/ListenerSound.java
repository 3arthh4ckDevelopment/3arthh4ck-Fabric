package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

final class ListenerSound extends
        ModuleListener<AutoFish, PacketEvent.Receive<PlaySoundS2CPacket>>
{
    public ListenerSound(AutoFish module)
    {
        super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlaySoundS2CPacket> event)
    {
        PlaySoundS2CPacket packet = event.getPacket();
        if (packet.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH))
        {
            FishingBobberEntity fish = mc.player.fishHook;
            if (fish != null
                    && mc.player.equals(fish.getPlayerOwner())
                    && (module.range.getValue() == 0.0
                    || fish.getPos()
                    .distanceTo(new Vec3d(packet.getX(),
                            packet.getY(),
                            packet.getZ()))
                    <= module.range.getValue()))
            {
                module.splash = true;
            }
        }
    }

}