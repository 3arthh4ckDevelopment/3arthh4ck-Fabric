package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

final class ListenerDeath extends ModuleListener<Announcer, DeathEvent>
{
    public ListenerDeath(Announcer module)
    {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event)
    {
        if (module.autoEZ.getValue())
        {
            ClientPlayerEntity player = mc.player;
            //noinspection SuspiciousMethodCalls
            if (player != null
                && !player.equals(event.getEntity())
                && event.getEntity() instanceof PlayerEntity
                && (!module.friends.getValue()
                    || !Managers.FRIENDS.contains(event.getEntity()))
                && (!module.targetsOnly.getValue()
                    || module.targets.remove(event.getEntity()))
                && mc.player.distanceTo(event.getEntity()) <= 144)
            {
                module.announcements.put(AnnouncementType.Death,
                        new Announcement(event.getEntity().getName().getString(), 0));
                module.announcements.put(AnnouncementType.Totems, null);
            }
        }
    }

}
