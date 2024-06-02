package me.earth.earthhack.impl.modules.render.logoutspots;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.ConnectionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.render.logoutspots.mode.MessageMode;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.PlayerEntity;

final class ListenerLeave extends ModuleListener<LogoutSpots, ConnectionEvent.Leave>
{
    public ListenerLeave(LogoutSpots module)
    {
        super(module, ConnectionEvent.Leave.class);
    }

    private static final ModuleCache<AutoTrap> AUTO_TRAP =
            Caches.getModule(AutoTrap.class);
    @Override
    public void invoke(ConnectionEvent.Leave event)
    {
        if (event.getName() == mc.player.getName().getString())
            return;

        if (AUTO_TRAP.get().logOutSpot.getValue())
            if (AUTO_TRAP.get().isValid(event.getPlayer()))
                AUTO_TRAP.get().target = event.getPlayer();


        PlayerEntity player = event.getPlayer();
        if (module.message.getValue() != MessageMode.None)
        {
            String text = null;
            if (player != null)
            {
                text = String.format(TextColor.YELLOW
                                        + player.getName()
                                        + TextColor.RED
                                        + " just logged out, at: %sx, %sy, %sz.",
                                            MathUtil.round(player.getX(), 1),
                                            MathUtil.round(player.getY(), 1),
                                            MathUtil.round(player.getZ(), 1));
            }
            else if (module.message.getValue() != MessageMode.Render)
            {
                text = TextColor.YELLOW + event.getName() + TextColor.RED + " just logged out.";
            }

            if (text != null)
            {
                Managers.CHAT.sendDeleteMessageScheduled(text, event.getUuid().toString());
            }
        }

        if (player != null && (module.friends.getValue() || !Managers.FRIENDS.contains(player)))
        {
            LogoutSpot spot = new LogoutSpot(player);
            module.spots.put(player.getUuid(), spot);
        }
    }
}
