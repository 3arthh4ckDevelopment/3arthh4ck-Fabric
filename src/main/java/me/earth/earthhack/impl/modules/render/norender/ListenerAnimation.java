package me.earth.earthhack.impl.modules.render.norender;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;

final class ListenerAnimation extends
        ModuleListener<NoRender, PacketEvent.Receive<EntityAnimationS2CPacket>>
{
    public ListenerAnimation(NoRender module)
    {
        super(module, PacketEvent.Receive.class, EntityAnimationS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityAnimationS2CPacket> event)
    {
        if (module.critParticles.getValue()
                && (event.getPacket().getAnimationId() == 4
                    || event.getPacket().getAnimationId() == 5))
        {
            event.setCancelled(true);
        }
    }

}
