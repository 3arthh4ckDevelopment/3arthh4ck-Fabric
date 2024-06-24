package me.earth.earthhack.impl.core.mixins;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.IWorld;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld
{
    @Unique
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);
    // private static final ModuleCache<Packets> PACKETS =
    //         Caches.getModule(Packets.class);
    // private static final ModuleCache<BlockTweaks> BLOCK_TWEAKS =
    //         Caches.getModule(BlockTweaks.class);

    @Final
    @Shadow
    public boolean isClient;

    // @Override
    // @Invoker(value = "isChunkLoaded")
    public abstract boolean earthhack$isChunkLoaded(int x, int z);

    @Inject(
            method = "tickEntity(Ljava/util/function/Consumer;Lnet/minecraft/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 0
            )
    )
    public <T extends Entity> void tickEntityHook(Consumer<T> tickConsumer, T entity, CallbackInfo ci)
    {
        if (isClient)
        {
            UpdateEntitiesEvent event = new UpdateEntitiesEvent();
            Bus.EVENT_BUS.post(event);
        }
    }
}
