package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

// TODO: THIS!
// we could make it ai; this shouldn't be very hard
public class HealthManager extends SubscriberImpl implements Globals
{
    private volatile float lastAbsorption = -1.0f;
    private volatile float lastHealth     = -1.0f;

    public HealthManager()
    {
        this.listeners.add(new ReceiveListener<>(HealthUpdateS2CPacket.class, e ->
        {
            HealthUpdateS2CPacket packet = e.getPacket();
        }));
    }

    public float getLastHealth()
    {
        return lastHealth;
    }
}
