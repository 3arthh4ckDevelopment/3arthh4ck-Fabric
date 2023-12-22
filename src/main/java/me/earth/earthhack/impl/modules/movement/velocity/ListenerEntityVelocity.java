package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.impl.core.mixins.network.server.IEntityVelocityUpdateS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

final class ListenerEntityVelocity extends
        ModuleListener<Velocity, PacketEvent.Receive<EntityVelocityUpdateS2CPacket>>
{
    public ListenerEntityVelocity(Velocity module)
    {
        super(module,
                PacketEvent.Receive.class,
                -1000000,
                EntityVelocityUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityVelocityUpdateS2CPacket> event)
    {
        if (module.knockBack.getValue() && mc.player != null)
        {
            ChatUtil.sendMessage("[DEBUG] Received packet " + event.getPacket().getClass().getName()
                    + "," +
                    " expected EntityVelocityUpdateS2CPacket.");

            IEntityVelocityUpdateS2CPacket velocity =
                    (IEntityVelocityUpdateS2CPacket) event.getPacket();
            if (velocity.getId() == mc.player.getId())
            {
                if (module.horizontal.getValue() == 0
                        && module.vertical.getValue() == 0)
                {
                    event.setCancelled(true);
                }
                else
                {
                    velocity.setX((int) (velocity.getX()
                            * module.horizontal.getValue()));
                    velocity.setY((int) (velocity.getY()
                            * module.vertical.getValue()));
                    velocity.setZ((int) (velocity.getZ()
                            * module.horizontal.getValue()));
                }
            }
        }
    }

}
