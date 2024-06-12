package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ListenerSneak
        extends ModuleListener<Phase, PacketEvent.Send<ClientCommandC2SPacket>>
{
    public ListenerSneak(Phase module)
    {
        super(module, PacketEvent.Send.class, ClientCommandC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<ClientCommandC2SPacket> event)
    {
        if (event.getPacket().getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
                && module.isPhasing()
                && module.cancelSneak.getValue()
                && mc.options.sneakKey.isPressed())
        {
            event.setCancelled(true);
        }
    }
}