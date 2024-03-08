package me.earth.earthhack.impl.managers.thread.scheduler;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.MinecraftClient;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler extends SubscriberImpl implements Globals {
    private static final Scheduler INSTANCE = new Scheduler();

    private final Queue<Runnable> scheduled  = new LinkedList<>();
    private final Queue<Runnable> toSchedule = new LinkedList<>();
    private boolean executing;
    private int gameLoop;

    private Scheduler()
    {
        this.listeners.add(
                new EventListener<>
                        (GameLoopEvent.class, Integer.MAX_VALUE)
                {
                    @Override
                    public void invoke(GameLoopEvent event)
                    {
                        gameLoop  = ((IMinecraftClient) mc).earthhack$getGameLoop(); // will NOT :fire: work

                        executing = true;
                        CollectionUtil.emptyQueue(scheduled, Runnable::run);
                        executing = false;

                        CollectionUtil.emptyQueue(toSchedule, scheduled::add);
                    }
                });
    }

    /** @return the Singleton Instance of the Scheduler. */
    public static Scheduler getInstance()
    {
        return INSTANCE;
    }

    /**
     * {@link MinecraftClient#execute(Runnable)},
     * with one difference: If the call comes from the MainThread,
     * the runnable will be scheduled for the next gameloop, instead
     * of being run immediately.
     *
     * @param runnable the runnable to schedule.
     */
    public void schedule(Runnable runnable)
    {
        schedule(runnable, true);
    }

    /**
     * Will call {@link MinecraftClient#execute(Runnable)},
     * to call {@link Scheduler#schedule(Runnable, boolean)},
     * for the given Runnable and <tt>false</tt>.
     *
     * @param runnable the runnable to schedule.
     */
    public void scheduleAsynchronously(Runnable runnable)
    {
        mc.execute(() -> schedule(runnable, false));
    }

    /**
     * The same as {@link Scheduler#schedule(Runnable)},
     * but if checkGameLoop is <tt>false</tt> and this method
     * is called within a Runnable that was scheduled via
     * {@link MinecraftClient#execute(Runnable)}, the Runnable
     * will be executed this gameloop.
     *
     * @param runnable the runnable to schedule.
     * @param checkGameLoop if the current gameloop should be checked.
     *                      (Important when called from
     *                      {@link MinecraftClient#execute(Runnable)}).
     */
    public void schedule(Runnable runnable, boolean checkGameLoop)
    {
        if (mc.isOnThread())
        {
            if (executing || checkGameLoop
                    && gameLoop != ((IMinecraftClient) mc).earthhack$getGameLoop())
            {
                toSchedule.add(runnable);
            }
            else
            {
                scheduled.add(runnable);
            }
        }
        else
        {
            mc.execute(runnable);
        }
    }
}
