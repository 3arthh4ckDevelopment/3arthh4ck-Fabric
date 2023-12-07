package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.autolog.util.LogScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;

final class ListenerScreen
        extends ModuleListener<AutoLog, GuiScreenEvent<DisconnectedScreen>>
{
    public ListenerScreen(AutoLog module)
    {
        super(module, GuiScreenEvent.class, DisconnectedScreen.class);
    }

    @Override
    public void invoke(GuiScreenEvent<DisconnectedScreen> event)
    {
        if (module.awaitScreen)
        {
            module.awaitScreen = false;
            mc.setScreen(
                    new LogScreen(module, module.message, module.serverData));
            event.setCancelled(true);
        }
    }

}
