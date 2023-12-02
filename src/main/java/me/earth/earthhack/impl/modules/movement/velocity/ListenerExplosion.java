package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.IExplosionS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

final class ListenerExplosion extends
        ModuleListener<Velocity, PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerExplosion(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000000,
                ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        if (module.explosions.getValue())
        {
            IExplosionS2CPacket explosion = (IExplosionS2CPacket) event.getPacket();
            explosion.setX(explosion.getX() * module.horizontal.getValue());
            explosion.setY(explosion.getY() * module.vertical.getValue());
            explosion.setZ(explosion.getZ() * module.horizontal.getValue());
        }
    }

}
