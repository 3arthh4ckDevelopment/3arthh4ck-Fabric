package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.util.Hand;

final class ListenerAnimation extends
        ModuleListener<Spectate, PacketEvent.Receive<EntityAnimationS2CPacket>>
{
    public ListenerAnimation(Spectate module)
    {
        super(module, PacketEvent.Receive.class, EntityAnimationS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityAnimationS2CPacket> event)
    {
        event.addPostEvent(() ->
        {
            PlayerEntity playerSp = mc.player;
            if (playerSp != null && module.spectating)
            {
                PlayerEntity player = module.player;
                EntityAnimationS2CPacket packet = event.getPacket();
                if (player != null
                        && packet.getId() == player.getId())
                {
                    if (packet.getAnimationId() == 0)
                    {
                        playerSp.swingHand(Hand.MAIN_HAND);
                    }
                    else if (packet.getAnimationId() == 3)
                    {
                        playerSp.swingHand(Hand.OFF_HAND);
                    }
                }
            }
        });
    }

}
