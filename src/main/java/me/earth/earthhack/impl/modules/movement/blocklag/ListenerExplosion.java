package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

final class ListenerExplosion extends
        ModuleListener<BlockLag, PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerExplosion(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        if (module.scaleExplosion.getValue())
        {
            module.motionY = event.getPacket().getPlayerVelocityY();
            module.scaleTimer.reset();
        }
    }

}
