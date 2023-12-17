package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.core.ducks.gui.IChatHud;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;

final class ListenerGameLoop extends ModuleListener<Chat, GameLoopEvent>
{
    public ListenerGameLoop(Chat module)
    {
        super(module, GameLoopEvent.class);
    }

    @Override
    public void invoke(GameLoopEvent event)
    {
        if (!module.cleared && mc.inGameHud != null)
        {
            IChatHud chat = (IChatHud) mc.inGameHud.getChatHud();
            if (chat.getScrollPos() == 0)
                module.clearNoScroll();
        }

        if (!mc.isPaused() && mc.currentScreen == null && module.needsKit && mc.player != null) {
            module.needsKit = false;
            mc.player.networkHandler.sendChatMessage("/kit " + module.kitName.getValue());
            Managers.CHAT.sendDeleteMessage(module.kitName.getValue(), module.getName(), ChatIDs.COMMAND);
        }
    }

}
