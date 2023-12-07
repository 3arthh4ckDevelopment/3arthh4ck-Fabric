package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;

final class ListenerTotems extends
        ModuleListener<Announcer, PacketEvent.Receive<EntityStatusEffectS2CPacket>>
{
    public ListenerTotems(Announcer module)
    {
        super(module, PacketEvent.Receive.class, EntityStatusEffectS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityStatusEffectS2CPacket> event)
    {
        if (module.totems.getValue())
        {
            EntityStatusEffectS2CPacket packet = event.getPacket();
            ClientPlayerEntity player = mc.player;
            if (/*packet.getOpCode() == 35 &&  this should be the status effect but idk*/player != null)
            {

                Entity entity = Managers.ENTITIES.getEntity(packet.getEntityId());
                if (entity instanceof PlayerEntity
                        && (!module.friends.getValue() || !Managers.FRIENDS.contains((PlayerEntity) entity))
                        && !player.equals(entity))
                {
                    Announcement announcement = module.addWordAndIncrement
                            (AnnouncementType.Totems, entity.getName().getString());

                    announcement.setAmount(Managers.COMBAT.getPops(entity) + 1);
                }
            }
        }
    }

}
