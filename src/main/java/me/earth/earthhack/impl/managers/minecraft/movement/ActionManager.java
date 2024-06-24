package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ActionManager extends SubscriberImpl {
    private volatile boolean sneaking;
    private volatile boolean sprinting;

    public ActionManager()
    {
        this.listeners.add(
                new EventListener<PacketEvent.Post<ClientCommandC2SPacket>>
                        (PacketEvent.Post.class, ClientCommandC2SPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Post<ClientCommandC2SPacket> event)
                    {
                        switch (event.getPacket().getMode()) {
                            case START_SPRINTING -> sprinting = true;
                            case STOP_SPRINTING -> sprinting = false;
                            case PRESS_SHIFT_KEY -> sneaking = true;
                            case RELEASE_SHIFT_KEY -> sneaking = false;
                            default -> {
                            }
                        }
                    }
                });
    }

    /**
     * @return <tt>true</tt> if we are sprinting on the server.
     */
    public boolean isSprinting()
    {
        return sprinting;
    }

    /**
     * @return <tt>true</tt> if we are sneaking on the server.
     */
    public boolean isSneaking()
    {
        return sneaking;
    }
}
