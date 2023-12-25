package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

final class ListenerVelocity extends
        ModuleListener<BlockLag, PacketEvent.Receive<EntityVelocityUpdateS2CPacket>>
{
    public ListenerVelocity(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, EntityVelocityUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityVelocityUpdateS2CPacket> event)
    {
        if (!module.scaleVelocity.getValue())
        {
            return;
        }

        PlayerEntity playerSP = mc.player;
        if (playerSP != null
                && event.getPacket().getId() == playerSP.getId())
        {
            module.motionY = event.getPacket().getVelocityY() / 8000.0;
            module.scaleTimer.reset();
        }
    }

}
