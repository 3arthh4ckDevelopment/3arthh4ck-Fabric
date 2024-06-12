package me.earth.earthhack.impl.modules.movement.elytraflight;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<ElytraFlight, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(ElytraFlight module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.mode.getValue() == ElytraMode.Packet
                && mc.player.getEquippedStack(EquipmentSlot.CHEST)
                .getItem() == Items.ELYTRA)
        {
            module.lag = true;
        }
    }

}