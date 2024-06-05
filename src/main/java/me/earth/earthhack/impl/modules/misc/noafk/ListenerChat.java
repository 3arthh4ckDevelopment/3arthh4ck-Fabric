package me.earth.earthhack.impl.modules.misc.noafk;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

final class ListenerChat extends
        ModuleListener<NoAFK, PacketEvent.Receive<ChatMessageS2CPacket>>
{
    public ListenerChat(NoAFK module)
    {
        super(module, PacketEvent.Receive.class, ChatMessageS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ChatMessageS2CPacket> event)
    {
        if (module.autoReply.getValue())
        {
            String m = event.getPacket().unsignedContent().getString();
            if ((m.contains(module.color.getValue().getColor())
                    || module.color.getValue() == TextColor.Reset)
                        && m.contains(module.indicator.getValue()))
            {
                mc.getNetworkHandler().sendChatMessage(module.reply.getValue()
                        + module.message.getValue());
            }
        }
    }

}
