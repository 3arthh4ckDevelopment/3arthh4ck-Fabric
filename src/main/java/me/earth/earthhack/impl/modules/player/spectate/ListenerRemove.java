package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

final class ListenerRemove extends
        ModuleListener<Spectate, PacketEvent.Receive<EntitiesDestroyS2CPacket>>
{
    public ListenerRemove(Spectate module)
    {
        super(module, PacketEvent.Receive.class, EntitiesDestroyS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitiesDestroyS2CPacket> event)
    {
        if (module.spectating)
        {
            PlayerEntity player = module.player;
            if (player != null)
            {
                for (int id : event.getPacket().getEntityIds())
                {
                    if (id == player.getId())
                    {
                        mc.execute(() ->
                        {
                            module.disable();
                            ModuleUtil.sendMessage(module, TextColor.RED
                                + "The Player you spectated got removed.");
                        });

                        return;
                    }
                }
            }
        }
    }

}
