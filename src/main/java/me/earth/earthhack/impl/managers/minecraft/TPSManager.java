package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.ArrayDeque;

//TODO: Average/Current!
public class TPSManager extends SubscriberImpl implements Globals
{
    private final ArrayDeque<Float> queue = new ArrayDeque<>(20);
    private float currentTps;
    private long time;
    private float tps;

    public TPSManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<WorldTimeUpdateS2CPacket>> //TODO: check
                    (PacketEvent.Receive.class, WorldTimeUpdateS2CPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<WorldTimeUpdateS2CPacket> event)
            {
                if (time != 0)
                {
                    if (queue.size() > 20)
                    {
                        queue.poll();
                    }

                    currentTps = Math.max(0.0f, Math.min(20.0f, 20.0f * (1000.0f / (System.currentTimeMillis() - time))));
                    queue.add(currentTps);
                    float factor = 0.0f;
                    for (Float qTime : queue)
                    {
                        factor += Math.max(0.0f, Math.min(20.0f, qTime));
                    }

                    if (queue.size() > 0)
                    {
                        factor /= queue.size();
                    }

                    tps = factor;
                }

                time = System.currentTimeMillis();
            }
        });
    }

    public float getCurrentTps()
    {
        return currentTps;
    }

    public float getTps()
    {
        return tps;
    }

    public float getFactor()
    {
        return tps / 20.0f;
    }

}
