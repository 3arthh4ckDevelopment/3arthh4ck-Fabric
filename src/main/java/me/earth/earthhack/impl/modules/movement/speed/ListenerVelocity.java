package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Direction;

final class ListenerVelocity extends
        ModuleListener<Speed, PacketEvent.Receive<EntityVelocityUpdateS2CPacket>>
{
    public ListenerVelocity(Speed module)
    {
        super(module, PacketEvent.Receive.class, EntityVelocityUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityVelocityUpdateS2CPacket> event)
    {
        EntityVelocityUpdateS2CPacket packet = event.getPacket();
        PlayerEntity player = mc.player;
        if (player != null
                && packet.getId() == player.getId()
                && !module.directional.getValue()
                && module.velocity.getValue())
        {
            double speed = Math.sqrt(
                    packet.getVelocityX() * packet.getVelocityX()
                            + packet.getVelocityZ() * packet.getVelocityZ())
                    /  8000.0;

            module.lastExp = module.expTimer
                    .passed(module.coolDown.getValue())
                    ? speed
                    : (speed - module.lastExp);

            if (module.lastExp > 0)
            {
                module.expTimer.reset();
                mc.execute(() ->
                {
                    module.speed +=
                            module.lastExp * module.multiplier.getValue();

                    module.distance +=
                            module.lastExp * module.multiplier.getValue();

                    if (mc.player.getVelocity().getY() > 0
                            && module.vertical.getValue() != 0)
                    {
                        mc.player.setVelocity(
                                mc.player.getVelocity().withAxis(Direction.Axis.Y,
                                mc.player.getVelocity().getY() * module.vertical.getValue())
                        );
                    }
                });
            }
        }
    }

}
