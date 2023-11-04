package me.earth.earthhack.impl.modules.client.commands;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import net.minecraft.client.gui.screen.ChatScreen;

final class ListenerKeyboard extends ModuleListener<Commands, KeyboardEvent>
{
    public ListenerKeyboard(Commands module)
    {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event)
    {
        if (module.prefixBind.getValue()
                && event.getEventState()
                && event.getCharacter() == module.prefixChar)
        {
            Scheduler.getInstance().schedule(() ->
                    mc.setScreen(new ChatScreen(Commands.getPrefix())));
        }
    }
}
