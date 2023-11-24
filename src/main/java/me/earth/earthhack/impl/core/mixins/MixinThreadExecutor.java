package me.earth.earthhack.impl.core.mixins;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ThreadExecutor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Queue;

/**
 * Utility class for {@link ThreadExecutor}.
 * Made in place for runScheduledTasks in {@link me.earth.earthhack.impl.core.ducks.IMinecraftClient},
 * because their implementation has been moved to ThreadExecutor since 1.12.
 * The usage is practically the same, and doesn't require anything special.
 */
@Mixin(ThreadExecutor.class)
public abstract class MixinThreadExecutor {

    @Final
    @Shadow
    private static Logger LOGGER = LogUtils.getLogger();

    @Final
    @Shadow
    private final Queue<? extends Runnable> tasks = Queues.newConcurrentLinkedQueue();

    // maybe add the debugging logger?
    @Invoker(value = "runTasks")
    public void runTasks() {
        synchronized (this.tasks)
        {
            while (!this.tasks.isEmpty())
            {
                Util.debugRunnable(LOGGER.getName(), this.tasks.poll());
            }
        }
    }
}
