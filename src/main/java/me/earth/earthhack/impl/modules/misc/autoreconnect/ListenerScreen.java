package me.earth.earthhack.impl.modules.misc.autoreconnect;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.gui.screen.DisconnectedScreen;

final class ListenerScreen extends
        ModuleListener<AutoReconnect, GuiScreenEvent<DisconnectedScreen>>
{
    public ListenerScreen(AutoReconnect module)
    {
        super(module, GuiScreenEvent.class, -1000, DisconnectedScreen.class);
    }

    @Override
    public void invoke(GuiScreenEvent<DisconnectedScreen> event)
    {
        if (!event.isCancelled()/* && !PingBypass.isConnected()*/) {
            if (module.connected) {
                module.setConnected(false);
            } else {
                module.onGuiDisconnected(event.getScreen());
                event.setCancelled(true);
            }
        }
    }

}
