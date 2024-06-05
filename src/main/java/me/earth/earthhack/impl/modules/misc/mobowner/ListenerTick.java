package me.earth.earthhack.impl.modules.misc.mobowner;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;

import java.util.UUID;

final class ListenerTick extends ModuleListener<MobOwner, TickEvent>
{
    public ListenerTick(MobOwner module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.world != null)
        {
            for (Entity entity : mc.world.getEntities())
            {
                if (entity != null && !entity.shouldRenderName())
                {
                    if (entity instanceof TameableEntity tameable)
                    {
                        if (tameable.isTamed())
                        {
                            renderNametag(entity, tameable.getOwnerUuid());
                        }
                    }
                    else if (entity instanceof HorseEntity horse)
                    {
                        if (horse.isTame())
                        {
                            renderNametag(entity, horse.getOwnerUuid());
                        }
                    }
                }
            }
        }
    }

    private void renderNametag(Entity entity, UUID id)
    {
        if (id != null)
        {
            if (module.cache.containsKey(id))
            {
                String owner = module.cache.get(id);
                if (owner != null)
                {
                    entity.setCustomNameVisible(true);
                    entity.setCustomName(Text.of(owner));
                }
            }
            else
            {
                module.cache.put(id, null);
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.NAME, id)
                {
                    @Override
                    public void onSuccess()
                    {
                        mc.execute(() -> module.cache.put(id, name));
                    }

                    @Override
                    public void onFailure()
                    {
                        mc.execute(() -> module.cache.put(id, null));
                    }
                });
            }
        }
    }

}
