package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<Tracker, PacketEvent.Receive<EntitySpawnS2CPacket>>
{
    public ListenerSpawnObject(Tracker module)
    {
        super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        EntitySpawnS2CPacket p = event.getPacket();
        if (mc.world == null || mc.player == null)
        {
            return;
        }

        //if (p.getType() == 51)
        if (p.getEntityData() == 51)
        {
            BlockPos pos = new BlockPos((int) p.getX(), (int) p.getY(), (int) p.getZ());
            if (!module.placed.remove(pos))
            {
                module.crystals.incrementAndGet();
            }
        }
        else if (p.getEntityData() == 75)
        {
            if (module.awaitingExp.get() > 0)
            {
                if (mc.player.squaredDistanceTo(p.getX(), p.getY(), p.getZ()) < 16)
                {
                    module.awaitingExp.decrementAndGet();
                }
                else
                {
                    module.exp.incrementAndGet();
                }
            }
            else
            {
                module.exp.incrementAndGet();
            }
        }
    }

}
