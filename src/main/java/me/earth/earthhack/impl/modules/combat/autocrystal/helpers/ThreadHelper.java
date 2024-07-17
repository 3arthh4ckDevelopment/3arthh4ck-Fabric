package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AbstractCalculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.Calculation;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps with processing {@link Calculation}s.
 */
public class ThreadHelper implements Globals
{
    private final StopWatch threadTimer = new StopWatch();
    private final Setting<Boolean> multiThread;
    private final Setting<Boolean> mainThreadThreads;
    private final Setting<Integer> threadDelay;
    private final Setting<RotationThread> rotationThread;
    private final Setting<ACRotate> rotate;
    private final AutoCrystal module;

    private volatile AbstractCalculation<?> currentCalc;

    public ThreadHelper(AutoCrystal module,
                        Setting<Boolean> multiThread,
                        Setting<Boolean> mainThreadThreads,
                        Setting<Integer> threadDelay,
                        Setting<RotationThread> rotationThread,
                        Setting<ACRotate> rotate)
    {
        this.module = module;
        this.multiThread = multiThread;
        this.mainThreadThreads = mainThreadThreads;
        this.threadDelay = threadDelay;
        this.rotationThread = rotationThread;
        this.rotate = rotate;
    }

    public synchronized void start(AbstractCalculation<?> calculation,
                                   boolean multiThread)
    {
        if (!module.isPingBypass()
                && threadTimer.passed(threadDelay.getValue())
                && (currentCalc == null || currentCalc.isFinished()))
        {
            currentCalc = calculation;
            execute(currentCalc, multiThread);
        }
    }

    public synchronized void startThread(BlockPos...blackList)
    {
        if (mc.world == null
            || mc.player == null
            || module.isPingBypass()
            || !threadTimer.passed(threadDelay.getValue())
            || currentCalc != null && !currentCalc.isFinished())
        {
            return;
        }

        startThread(Managers.ENTITIES.getEntities(!mc.isOnThread()),
                    Managers.ENTITIES.getPlayers(!mc.isOnThread()),
                    blackList);
    }

    public synchronized void startThread(boolean breakOnly, boolean noBreak, BlockPos...blackList)
    {
        if (mc.world == null
                || mc.player == null
                || module.isPingBypass()
                || !threadTimer.passed(threadDelay.getValue())
                || currentCalc != null && !currentCalc.isFinished())
        {
            return;
        }

        startThread(Managers.ENTITIES.getEntities(!mc.isOnThread()),
                Managers.ENTITIES.getPlayers(!mc.isOnThread()),
                breakOnly,
                noBreak,
                blackList);
}

    private void startThread(List<Entity> entities,
                             List<PlayerEntity> players,
                             boolean breakOnly,
                             boolean noBreak,
                             BlockPos...blackList)
    {
        currentCalc = new Calculation(module, entities, players, breakOnly, noBreak, blackList);
        execute(currentCalc, multiThread.getValue());
    }

    private void startThread(List<Entity> entities,
                             List<PlayerEntity> players,
                             BlockPos...blackList)
    {
        currentCalc = new Calculation(module, entities, players, blackList);
        execute(currentCalc, multiThread.getValue());
    }

    private void execute(AbstractCalculation<?> calculation,
                         boolean multiThread)
    {
        if (multiThread)
        {
            Managers.THREAD.submitRunnable(calculation);
            threadTimer.reset();
        }
        else
        {
            threadTimer.reset();
            calculation.run();
        }
    }

    public void schedulePacket(PacketEvent.Receive<?> event)
    {
        if ((multiThread.getValue() || mainThreadThreads.getValue())
            && (rotate.getValue() == ACRotate.None
                || rotationThread.getValue() != RotationThread.Predict))
        {
            event.addPostEvent(this::startThread);
        }
    }

    /** @return the currently running, or last finished calculation. */
    public AbstractCalculation<?> getCurrentCalc()
    {
        return currentCalc;
    }

    public void reset()
    {
        currentCalc = null;
    }

}
