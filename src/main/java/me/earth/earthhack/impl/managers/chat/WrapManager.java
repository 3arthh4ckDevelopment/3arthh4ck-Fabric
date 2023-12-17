package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.network.message.MessageSignatureData;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages wrapping for
 * {@link AbstractTextComponent}s.
 */
//TODO: This is so bad
public class WrapManager extends SubscriberImpl implements Globals
{
    private final Map<ChatLineReferenceMap, AbstractTextComponent> components =
            new ConcurrentHashMap<>();

    public WrapManager()
    {
        this.listeners.add(
            new EventListener<>(TickEvent.class)
            {
                @Override
                public void invoke(TickEvent event)
                {
                    onTick();
                }
            });
        this.listeners.add(
            new EventListener<>(DisconnectEvent.class)
            {
                @Override
                public void invoke(DisconnectEvent event)
                {
                    mc.execute(() -> clear());
                }
            });
        this.listeners.add(
            new EventListener<>
                    (WorldClientEvent.Load.class)
            {
                @Override
                public void invoke(WorldClientEvent.Load event)
                {
                    mc.execute(() -> clear());
                }
            });
    }

    private void clear()
    {
        if (mc.inGameHud != null)
        {
            for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent>
                    entry : components.entrySet())
            {
                mc.inGameHud
                  .getChatHud()
                  .removeMessage(entry.getKey().getSignature());
            }
        }

        components.clear();
    }

    private void onTick()
    {
        for (Map.Entry<ChatLineReferenceMap, AbstractTextComponent> entry :
                components.entrySet())
        {
            if (entry.getKey().isEmpty()
                    || !entry.getValue().isWrapping()
                    || mc.inGameHud == null)
            {
                components.remove(entry.getKey());
            }
            else
            {
                ((IChatHud) mc.inGameHud.getChatHud())
                    .replace(entry.getValue(),
                             entry.getKey().getId(),
                             true,
                             false);
            }
        }
    }

    public void registerComponent(AbstractTextComponent component,
                                  ChatHudLine...references)
    {
        components.put(new ChatLineReferenceMap(references), component);
    }

    private static class ChatLineReferenceMap
            extends WeakHashMap<ChatHudLine, Boolean>
    {
        private int id = ChatIDs.NONE;
        private MessageSignatureData sig;

        public ChatLineReferenceMap(ChatHudLine...references)
        {
            if (references != null)
            {
                for (ChatHudLine line : references)
                {
                    if (line != null)
                    {
                        super.put(line, true);
                        id = line.creationTick();
                        sig = line.signature();
                    }
                }
            }
        }

        public int getId()
        {
            return id;
        }

        public MessageSignatureData getSignature()
        {
            return sig;
        }

        @Override
        public int hashCode()
        {
            return id;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof ChatLineReferenceMap)
            {
                return ((ChatLineReferenceMap) o).id == this.id;
            }

            return false;
        }
    }

}
