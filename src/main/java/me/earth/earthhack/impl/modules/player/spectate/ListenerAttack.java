package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.core.mixins.network.client.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

final class ListenerAttack extends
        ModuleListener<Spectate, PacketEvent.Send<PlayerInteractEntityC2SPacket>>
{
    public ListenerAttack(Spectate module)
    {
        super(module, PacketEvent.Send.class, PlayerInteractEntityC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractEntityC2SPacket> event)
    {
        if (((IPlayerInteractEntityC2S) event.getPacket()).getEntityID()
                == mc.player.getId())
        {
            event.setCancelled(true);
        }
    }

}
