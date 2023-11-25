package me.earth.earthhack.impl.core.mixins;

import com.mojang.datafixers.DataFixer;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.render.RenderTickCounter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClient
{
    /*              not implemented yet
    private static final ModuleCache<Sorter> SORTER =
            Caches.getModule(Sorter.class);
    private static final ModuleCache<MultiTask> MULTI_TASK =
            Caches.getModule(MultiTask.class);
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);
    private static final ModuleCache<AutoConfig> CONFIG =
            Caches.getModule(AutoConfig.class);
    */

    @Unique
    private static boolean isEarthhackRunning = true;
    @Unique
    private int gameLoop = 0;

    /**
     * Sets the Window Title of Minecraft.
     * Applied through Mixin due to Display.setTitle being deprecated.
     * @param info the return value (String).
     */
    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> info)
    {
        info.setReturnValue(Earthhack.NAME + " - " + Earthhack.VERSION);
    }

    // can be done with AW, but I'll just do it like this (for now maybe).
    @Override
    @Accessor(value = "itemUseCooldown")
    public abstract int getRightClickDelay();

    @Override
    @Accessor(value = "itemUseCooldown")
    public abstract void setRightClickDelay(int delay);

    @Override
    @Accessor(value = "renderTickCounter")
    public abstract RenderTickCounter getTimer();


    @Override
    public int getGameLoop() {
        return gameLoop;
    }

    @Override
    public boolean isEarthhackRunning() {
        return isEarthhackRunning;
    }

    @Override
    @Accessor(value = "dataFixer")
    public abstract DataFixer getDataFixer();

    @Inject(
        method = "stop",
        at = @At("HEAD"))
    public void minecraftShutdownHook(CallbackInfo ci) {
        Earthhack.getLogger().info("Shutting down 3arthh4ck.");
        Bus.EVENT_BUS.post(new ShutDownEvent());

        try
        {
            Managers.CONFIG.saveAll();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Managers.THREAD.shutDown();
        isEarthhackRunning = false;
    }
}
