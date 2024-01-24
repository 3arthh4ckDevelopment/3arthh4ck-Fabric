package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.misc.chat.util.SuffixMode;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class ListenerChatMessage extends ModuleListener<Chat, PacketEvent.Send<?>> {
    public ListenerChatMessage(Chat module){
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> e)
    {
        if (mc.player != null && mc.world != null) {
            if (module.suffixMode.getValue() != SuffixMode.None
                && e.getPacket() instanceof ChatMessageC2SPacket pack) // no need for instanceof check I think
            {
                String message = pack.chatMessage();
                System.out.println("[MEGAULTRADEBUGMAXXING] " + message);
                if (!message.startsWith(Commands.getPrefix())) {
                    if (module.suffixWhispers.getValue() && (message.startsWith("/msg") || message.startsWith("/r") || message.startsWith("/w") || message.startsWith("/tell") || message.startsWith("/l"))) {
                        e.setCancelled(true);
                        sendMessage(message);
                    } else if (!message.startsWith("/")) {
                        e.setCancelled(true);
                        sendMessage(message);
                    }
                }
            }
        }
    }

    public void sendMessage(String message)
    {
        String suffix = module.suffixMode.getValue() == SuffixMode.Custom
                ? module.customSuffix.getValue()
                : module.suffixMode.getValue().getSuffix();
        // System.out.println(suffix);  // ?!??!?!??!?!?? log spam
        mc.player.networkHandler.sendChatMessage(message  + " | " + module.suffixMode.getValue().getSuffix());
    }
}
