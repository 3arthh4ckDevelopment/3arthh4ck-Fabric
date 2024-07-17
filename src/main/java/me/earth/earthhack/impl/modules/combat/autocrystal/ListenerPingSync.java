package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.ServerUtil;

final class ListenerPingSync extends ModuleListener<AutoCrystal, TickEvent> {
    public ListenerPingSync(AutoCrystal module) {
        super(module, TickEvent.class);
    }

    long pingNoPingSpoof;
    long ping;

    public void invoke(TickEvent e)
    {
        if(mc.world == null) return;
        if(mc.player == null) return;
        if(module.target == null) return;   // We don't need to actively PingSync while not targeting anyone I think
        if(false/*AutoCrystal.PINGBYPASS.isEnabled()*/ && module.ignorePingBypass.getValue()) return;

        pingNoPingSpoof = ServerUtil.getPingNoPingSpoof();
        ping = ServerUtil.getPing();

        if(module.pingSync.getValue())
        {
            if(module.absolutePingSync.getValue())
            { // Do we need Math.round() here? I think we can just make these variables integers, so we don't need rounding...
                module.placeTimer.reset(getCorrectPing() / 100 * Math.round((float) module.pingSyncStrength.getValue()));
                module.breakTimer.reset(getCorrectPing() / 100 * Math.round((float) module.pingSyncStrength.getValue()) - 2);
            }
            else
            {
                module.placeTimer.reset(getCorrectPing() / 100 * Math.round((float) module.pingSyncStrength.getValue()));
                module.breakTimer.reset(getCorrectPing() / 100 * Math.round((float) module.pingSyncStrength.getValue()) - Math.round(module.pingSyncRemoval.getValue()));
            }
        }
    }

    private long getCorrectPing()
    {
        return module.ignorePingspoof.getValue()
                ? pingNoPingSpoof
                : ping;
    }

}
