package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;

final class ListenerEntityEquipment extends
        ModuleListener<KillAura, PacketEvent.Receive<EntityEquipmentUpdateS2CPacket>>
{
    public ListenerEntityEquipment(KillAura module)
    {
        super(module, PacketEvent.Receive.class, EntityEquipmentUpdateS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntityEquipmentUpdateS2CPacket> event)
    {
        // this is surely done wrong, feel free to correct this listener
        EntityEquipmentUpdateS2CPacket packet = event.getPacket();
        if (packet.getEquipmentList().get(1).getFirst().getEntitySlotId() == 1
             && module.cancelEntityEquip.getValue()
             && packet.getEquipmentList().get(0).getSecond().getItem() instanceof AirBlockItem
             && mc.player.getOffHandStack().getItem() instanceof ShieldItem)
        {
            event.setCancelled(true);
        }
    }

}
