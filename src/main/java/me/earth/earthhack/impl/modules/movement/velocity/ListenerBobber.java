package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

final class ListenerBobber extends
        ModuleListener<Velocity, PacketEvent.Receive<EntityStatusS2CPacket>>
{
    public ListenerBobber(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                EntityStatusS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityStatusS2CPacket> event)
    {
        if (module.bobbers.getValue())
        {
            EntityStatusS2CPacket packet = event.getPacket();
            if (packet.getStatus() == 31 && !event.isCancelled())
            {
                event.setCancelled(true);
                mc.execute(() ->
                {
                    if (mc.getNetworkHandler() == null)
                    {
                        return;
                    }

                    Entity entity = packet.getEntity(mc.world);
                    if (entity instanceof FishingBobberEntity bobber)
                    {
                        if (bobber.getHookedEntity() != null
                                && mc.getNetworkHandler() != null
                                && !bobber.getHookedEntity().equals(mc.player))
                        {
                            packet.apply(mc.getNetworkHandler());
                        }
                    }
                    else
                    {
                        packet.apply(mc.getNetworkHandler());
                    }
                });
            }
        }
    }

}
