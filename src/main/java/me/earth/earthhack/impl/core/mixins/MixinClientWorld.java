package me.earth.earthhack.impl.core.mixins;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.IClientWorld;
import me.earth.earthhack.impl.event.events.network.EntityChunkEvent;
import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import net.minecraft.block.Block;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld implements IClientWorld {

    @Unique
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);

    @Shadow public abstract Entity getEntityById(int id);
    @Shadow @Final private PendingUpdateManager pendingUpdateManager;

    @Override
    public PendingUpdateManager earthhack$getPendingUpdateManager() {
        return pendingUpdateManager;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void constructorHook(CallbackInfo callbackInfo)
    {
        Bus.EVENT_BUS.post(new WorldClientEvent.Load(
                ClientWorld.class.cast(this)));
    }

    @Inject(method = "randomBlockDisplayTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"),
            cancellable = true)
    public void randomBlockDisplayTickHook(int centerX,
                                           int centerY,
                                           int centerZ,
                                           int radius,
                                           Random random,
                                           Block block,
                                           BlockPos.Mutable pos,
                                           CallbackInfo ci)
    {
        if (NO_RENDER.returnIfPresent(NoRender::showBarriers, false)) {
            ci.cancel();
        }
    }

    @Inject(method = "addEntity", at = @At("HEAD"))
    public void onEntityAdded(Entity entity, CallbackInfo info) {
        Bus.EVENT_BUS.post(new EntityChunkEvent(
                Stage.PRE,
                entity));
    }

    @Inject(method = "removeEntity", at = @At("HEAD"))
    public void onEntityRemoved(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        Bus.EVENT_BUS.post(new EntityChunkEvent(
                Stage.POST,
                getEntityById(entityId)));
    }
}
