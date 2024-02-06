package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

final class ListenerUseEntity
        extends ModuleListener<AutoCrystal, PacketEvent.Post<PlayerInteractEntityC2SPacket>>
{
    public ListenerUseEntity(AutoCrystal module)
    {
        super(module,
                PacketEvent.Post.class,
                Integer.MAX_VALUE,
                PlayerInteractEntityC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractEntityC2SPacket> event)
    {
        Entity entity = ((IPlayerInteractEntityC2S) event.getPacket())
                                                  .getAttackedEntity();
        if (entity == null)
        {
            entity = mc.world.getEntityById(((IPlayerInteractEntityC2S) event.getPacket()).getEntityID()); // todo : serverworld???????
            if (entity == null)
            {
                return;
            }
        }

        module.serverTimeHelper
              .onUseEntity(event.getPacket(),
                           entity);
    }

}
