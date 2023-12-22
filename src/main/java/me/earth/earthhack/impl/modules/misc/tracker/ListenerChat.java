package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

final class ListenerChat extends
        ModuleListener<Tracker, PacketEvent.Receive<ChatMessageS2CPacket>>
{
    public ListenerChat(Tracker module)
    {
        super(module, PacketEvent.Receive.class, ChatMessageS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ChatMessageS2CPacket> event)
    {
        if (module.autoEnable.getValue()
                && !module.awaiting
                && !module.isEnabled())
        {
            String s = event.getPacket().unsignedContent().getString();
            if (!s.contains("<") // must be a message by the server
                    && (s.contains("has accepted your duel request")
                        || s.contains("Accepted the duel request from")))
            {
                Scheduler.getInstance().scheduleAsynchronously(() ->
                {
                    ModuleUtil.sendMessage(module,
                        TextColor.LIGHT_PURPLE
                            + "Duel accepted. Tracker will enable in "
                            + TextColor.WHITE
                            + "5.0"
                            + TextColor.LIGHT_PURPLE
                            + " seconds!");

                    module.timer.reset();
                    module.awaiting = true;
                });
            }
        }
    }

}
