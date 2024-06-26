package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.screen.DeathScreen;

final class ListenerScreens extends
        ModuleListener<AutoRespawn, GuiScreenEvent<DeathScreen>>
{
    public ListenerScreens(AutoRespawn module)
    {
        super(module, GuiScreenEvent.class, DeathScreen.class);
    }

    @Override
    public void invoke(GuiScreenEvent<DeathScreen> event)
    {
        if (mc.player != null)
        {
            if (module.coords.getValue())
            {
                ChatUtil.sendMessage(TextColor.RED
                                + "You died at "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.getX(), 2)
                                + TextColor.RED
                                + "x, "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.getY(), 2)
                                + TextColor.RED
                                + "y, "
                                + TextColor.WHITE
                                + MathUtil.round(mc.player.getZ(), 2)
                                + TextColor.RED
                                + "z.", "death");
            }

            mc.player.requestRespawn();
            event.setCancelled(true);
        }
    }

}
