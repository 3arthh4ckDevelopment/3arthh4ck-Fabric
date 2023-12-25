package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

/**
 * Some servers block certain packets, especially
 * PlayerInteractEntityC2SPacket for around 10 ticks (~ 500 ms) after you
 * switched your mainhand slot. If you attack during this time you
 * might flag the anticheat. This class manages the time that
 * passed after the last switch.
 */
public class SwitchManager extends SubscriberImpl
{
    private final StopWatch timer = new StopWatch();
    private volatile int last_slot;

    public SwitchManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Post<UpdateSelectedSlotC2SPacket>>
                (PacketEvent.Post.class, UpdateSelectedSlotC2SPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Post<UpdateSelectedSlotC2SPacket> event)
            {
                timer.reset();
                last_slot = event.getPacket().getSelectedSlot();
            }
        });
        this.listeners.add(
            new EventListener<PacketEvent.Receive<UpdateSelectedSlotS2CPacket>>
                (PacketEvent.Receive.class, UpdateSelectedSlotS2CPacket.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<UpdateSelectedSlotS2CPacket> event)
            {
                last_slot = event.getPacket().getSlot();
            }
        });
    }

    /**
     * @return the time in ms that passed since the last
     *         {@link UpdateSelectedSlotC2SPacket} has been sent.
     */
    public long getLastSwitch()
    {
        return timer.getTime();
    }

    /**
     * @return the last slot reported to the server.
     */
    public int getSlot()
    {
        return last_slot;
    }

}
