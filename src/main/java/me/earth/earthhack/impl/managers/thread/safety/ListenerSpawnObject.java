package me.earth.earthhack.impl.managers.thread.safety;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerSpawnObject extends
        ModuleListener<SafetyManager,
                PacketEvent.Receive<EntitySpawnS2CPacket>>
{
    public ListenerSpawnObject(SafetyManager manager)
    {
        super(manager, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        EntitySpawnS2CPacket p = event.getPacket();
        if (p.getEntityData() == 51 && mc.player != null)
        {
            if (DamageUtil.calculate(
                    BlockPos.ofFloored(p.getX(), p.getY(), p.getZ()).down())
                        > module.damage.getValue())
            {
                module.setSafe(false);
            }
        }
    }

}
