package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.gui.screen.DeathScreen;

final class ListenerScreens extends
        ModuleListener<Chat, GuiScreenEvent<DeathScreen>>
{
    public ListenerScreens(Chat module)
    {
        super(module, GuiScreenEvent.class, DeathScreen.class);
    }

    @Override
    public void invoke(GuiScreenEvent<DeathScreen> event)
    {
        if (mc.player != null || mc.world != null || mc.currentScreen != null)
        {
            if (module.kit.getValue()) {
                module.kitTimer.reset();
                module.needsKit = true;
            }
        }
    }

}
