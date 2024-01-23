package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

final class ListenerTotems extends
        ModuleListener<Notifications, PacketEvent.Receive<EntityStatusS2CPacket>>
{
    public ListenerTotems(Notifications module)
    {
        super(module, PacketEvent.Receive.class, EntityStatusS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityStatusS2CPacket> event)
    {
        switch (event.getPacket().getStatus()) {
            case 3 -> mc.execute(() ->
            {
                if (mc.world != null) {
                    Entity entity = event.getPacket().getEntity(mc.world);
                    if (entity instanceof PlayerEntity) {
                        int pops = Managers.COMBAT.getPops(entity);
                        if (pops > 0) {
                            module.onDeath(entity,
                                    Managers.COMBAT.getPops(entity));
                        }
                    }
                }
            });
            case 35 -> mc.execute(() ->
            {
                Entity entity = event.getPacket().getEntity(mc.world);
                if (entity instanceof PlayerEntity) {
                    module.onPop(entity,
                            Managers.COMBAT.getPops(entity) + 1);
                }
            });
            default -> { /* Do nothing.*/ }
        }
    }

}
