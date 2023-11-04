package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages the legitimacy of windowClicks against NCP.
 * Also manages the time that passed since the last
 * LagBack via {@link EntityPositionS2CPacket}.
 */
@SuppressWarnings("ConstantConditions")
public class NCPManager extends SubscriberImpl implements Globals
{
    private final AtomicLong lagTimer  = new AtomicLong();
    private final StopWatch clickTimer = new StopWatch();
    private boolean endedSprint;
    private boolean endedSneak;
    private boolean windowClicks;
    private boolean strict;

    /** Constructs a new NCPManager. */
    public NCPManager()
    {
        this.listeners.add(
                new EventListener<PacketEvent.Receive<EntityPositionS2CPacket>>
                        (PacketEvent.Receive.class,
                                Integer.MAX_VALUE,
                                EntityPositionS2CPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Receive<EntityPositionS2CPacket> event)
                    {
                        lagTimer.set(System.currentTimeMillis());
                    }
                });
        this.listeners.add(
                new EventListener<>
                        (WorldClientEvent.Load.class)
                {
                    @Override
                    public void invoke(WorldClientEvent.Load event)
                    {
                        endedSneak   = false;
                        endedSprint  = false;
                        windowClicks = false;
                    }
                });
        this.listeners.add(
                new EventListener<PacketEvent.Send<ClickSlotC2SPacket>>
                        (PacketEvent.Send.class, -1000, ClickSlotC2SPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Send<ClickSlotC2SPacket> event)
                    {
                        if (!isStrict() || event.isCancelled())
                        {
                            return;
                        }

                        if (mc.player.isBlocking())
                        {
                            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                                    mc.interactionManager.stopUsingItem(mc.player));
                        }

                        if (Managers.ACTION.isSneaking())
                        {
                            endedSneak = true;
                            mc.player.networkHandler.sendPacket(
                                    new PlayerActionC2SPacket(mc.player,
                                            PlayerActionC2SPacket.Action.STOP_SNEAKING));
                        }

                        if (Managers.ACTION.isSprinting())
                        {
                            endedSprint = true;
                            mc.player.networkHandler.sendPacket(
                                    new PlayerActionC2SPacket(mc.player,
                                            PlayerActionC2SPacket.Action.STOP_SPRINTING));
                        }
                    }
                });
        this.listeners.add(
                new EventListener<PacketEvent.Post<ClickSlotC2SPacket>>
                        (PacketEvent.Post.class, -1000, ClickSlotC2SPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Post<ClickSlotC2SPacket> event)
                    {
                        clickTimer.reset();
                        if (!windowClicks && isStrict())
                        {
                            release();
                        }
                    }
                });
    }

    public StopWatch getClickTimer()
    {
        return clickTimer;
    }

    /**
     * @return <tt>true</tt> if NCP-Strict is active.
     */
    public boolean isStrict()
    {
        return strict;
    }

    /**
     * @param strict set NCP-Strict to this value.
     */
    public void setStrict(boolean strict)
    {
        if (this.strict && !strict)
        {
            releaseMultiClick();
        }

        this.strict = strict;
    }

    /**
     * Marks that NCP-Strict should expect multiple
     * WindowClicks in a short period of time and
     * not spam packets. Always call
     * {@link NCPManager#releaseMultiClick()}
     * afterwards.
     */
    public void startMultiClick()
    {
        this.windowClicks = true;
    }

    /**
     * Call after {@link NCPManager#startMultiClick()}, to
     * end a streak of multiple windowClicks and send the
     * packets.
     */
    public void releaseMultiClick()
    {
        this.windowClicks = false;
        release();
    }

    /**
     * Returns <tt>true</tt> if more time than the given delay in
     * milliseconds passed since the last {@link EntityPositionS2CPacket}
     * arrived at our client.
     *
     * @param ms the delay in ms to check.
     */
    public boolean passed(int ms)
    {
        return System.currentTimeMillis() - lagTimer.get() >= ms;
    }

    /**
     * @return the {@link System#currentTimeMillis()} of the last lag.
     */
    public long getTimeStamp()
    {
        return lagTimer.get();
    }

    /**
     * Resets the LagTimer. {@link NCPManager#passed(int)} is affected.
     */
    public void reset()
    {
        lagTimer.set(System.currentTimeMillis());
    }

    /**
     * Called after a windowClick, sends a SneakPacket
     * if we stopped sneaking for the windowClick and
     * a SprintPacket if we stopped sprinting.
     */
    private void release()
    {
        if (endedSneak)
        {
            endedSneak = false;
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, mc.player.getBlockPos(), null));
        }

        if (endedSprint)
        {
            endedSprint = false;
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, mc.player.getBlockPos(), null));
        }
    }

}
