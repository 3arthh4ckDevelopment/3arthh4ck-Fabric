package me.earth.earthhack.impl.core.mixins;

import com.mojang.datafixers.DataFixer;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.event.events.client.ClientInitEvent;
import me.earth.earthhack.impl.event.events.client.ShutDownEvent;
import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
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
    */
    @Unique
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);
    @Unique
    private static final ModuleCache<AutoConfig> CONFIG =
            Caches.getModule(AutoConfig.class);
    @Unique
    private static final ModuleCache<Management> MANAGEMENT =
            Caches.getModule(Management.class);
    @Unique
    private static boolean isEarthhackRunning = true;
    @Unique
    private int gameLoop = 0;

    @Shadow
    public ClientPlayerEntity player;


    /**
     * Sets the Window Title of Minecraft.
     * Applied through Mixin due to Display.setTitle being deprecated.
     * @param info the return value (String).
     */
    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> info)
    {
        info.setReturnValue(Earthhack.NAME + " - " + Earthhack.VERSION + (MANAGEMENT.get().toast.getValue()
                ? " " + MANAGEMENT.get().toastText.getValue()
                : ""));
    }

    @Override
    @Accessor(value = "itemUseCooldown")
    public abstract int earthhack$getRightClickDelay();

    @Override
    @Accessor(value = "itemUseCooldown")
    public abstract void earthhack$setRightClickDelay(int delay);

    @Override
    public int earthhack$getGameLoop() {
        return gameLoop;
    }

    @Override
    @Accessor(value = "fpsCounter")
    public abstract int earthhack$getFpsCounter();

    @Override
    public boolean earthhack$isRunning() {
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

    @Inject(method = "setScreen",
            at = @At("HEAD"),
            cancellable = true)
    private <T extends Screen> void setScreenHook(T screen, CallbackInfo info)
    {
        if (player == null && screen instanceof ChatScreen)
        {
            info.cancel();
            return;
        }

        GuiScreenEvent<T> event = new GuiScreenEvent<>(screen);
        Bus.EVENT_BUS.post(event, screen == null ? null : screen.getClass());

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderHead(boolean tick, CallbackInfo ci)
    {
        gameLoop++;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
            ordinal = 0,
            shift = At.Shift.AFTER))
    private void post_scheduledTasks(boolean tick, CallbackInfo ci)
    {
        Bus.EVENT_BUS.post(new GameLoopEvent());
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE))
    public void tickHook(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new TickEvent());
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;tick(Ljava/util/function/BooleanSupplier;)V",
                    shift = At.Shift.AFTER))
    private void postUpdateWorld(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new TickEvent.PostWorldTick());
    }

    @Inject(
            method = "tick",
            at = @At("RETURN"))
    public void tickReturnHook(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new TickEvent.Post());
    }

    @Inject(
            method = "onInitFinished",
            at = @At("HEAD")
    )
    public  void onInitFinishedHook(MinecraftClient.LoadingContext loadingContext, CallbackInfoReturnable<Runnable> cir) {
        Bus.EVENT_BUS.post(new ClientInitEvent());
    }

    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    public void startIntegratedServerHook(LevelStorage.Session session,
                                          ResourcePackManager dataPackManager,
                                          SaveLoader saveLoader, boolean newWorld,
                                          CallbackInfo ci)
    {
        if (CONFIG.isEnabled())
        {
            CONFIG.get().onConnect("singleplayer");
        }
    }

    @Inject(
        method = "getCameraEntity",
        at = @At("HEAD"),
        cancellable = true)
    public void getCameraEntityHook(CallbackInfoReturnable<Entity> cir)
    {
        if (SPECTATE.isEnabled())
        {
            cir.setReturnValue(SPECTATE.get().getRender());
        }
    }
}
