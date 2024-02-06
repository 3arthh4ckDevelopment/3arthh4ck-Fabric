package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.core.ducks.entity.IClientPlayerEntity;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PreMotionUpdateEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.compatibility.Compatibility;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends MixinAbstractClientPlayerEntity
        implements IClientPlayerEntity
{
    @Unique
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);
    // private static final ModuleCache<ElytraFlight> ELYTRA_FLIGHT =
    //         Caches.getModule(ElytraFlight.class);
    @Unique
    private static final ModuleCache<XCarry> XCARRY =
            Caches.getModule(XCarry.class);
    // private static final ModuleCache<Portals> PORTALS =
    //         Caches.getModule(Portals.class);
    // private static final SettingCache<Boolean, BooleanSetting, Portals> CHAT =
    //         Caches.getSetting(Portals.class, BooleanSetting.class, "Chat", true);
    @Unique
    private static final ModuleCache<Compatibility> ROTATION_BYPASS =
            Caches.getModule(Compatibility.class);

    @Shadow
    public Input input;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;

    @Unique private final MinecraftClient mc = MinecraftClient.getInstance();
    @Unique private MotionUpdateEvent.Riding riding;
    @Unique private MotionUpdateEvent motionEvent = new MotionUpdateEvent();

    @Override
    @Accessor(value = "lastX")
    public abstract double earthhack$getLastReportedX();

    @Override
    @Accessor(value = "lastBaseY")
    public abstract double earthhack$getLastReportedY();

    @Override
    @Accessor(value = "lastZ")
    public abstract double earthhack$getLastReportedZ();

    @Override
    @Accessor(value = "lastYaw")
    public abstract float earthhack$getLastReportedYaw();

    @Override
    @Accessor(value = "lastPitch")
    public abstract float earthhack$getLastReportedPitch();

    @Override
    @Accessor(value = "lastOnGround")
    public abstract boolean earthhack$getLastOnGround();

    @Override
    @Accessor(value = "lastX")
    public abstract void earthhack$setLastReportedX(double x);

    @Override
    @Accessor(value = "lastBaseY")
    public abstract void earthhack$setLastReportedY(double y);

    @Override
    @Accessor(value = "lastZ")
    public abstract void earthhack$setLastReportedZ(double z);

    @Override
    @Accessor(value = "lastYaw")
    public abstract void earthhack$setLastReportedYaw(float yaw);

    @Override
    @Accessor(value = "lastPitch")
    public abstract void earthhack$setLastReportedPitch(float pitch);

    @Override
    @Accessor(value = "ticksSinceLastPositionPacketSent")
    public abstract int earthhack$getPositionUpdateTicks();

    @Override
    @Accessor(value = "mountJumpStrength")
    public abstract void earthhack$setHorseJumpPower(float jumpPower);

    @Override
    public void earthhack$superUpdate()
    {
        super.tick();
    }

    @Override
    public void earthhack$invokeSendMovementPackets()
    {
        this.sendMovementPackets();
    }

    // @Override
    // public boolean isNoInterping()
    // {
    //     return false;
    // }

    @Shadow
    protected abstract void sendMovementPackets();

    /*
    ...
    ...
    ...
    ...
    ...
    ...
    ...
     */

    @Inject(
            method = "pushOutOfBlocks",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void pushOutOfBlocksHook(double x, double z, CallbackInfo ci)
    {
        BlockPushEvent event = new BlockPushEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }

    /**
     * {@link AbstractClientPlayerEntity#tick}
     */
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;"
                            + "tick()V",
                    shift = At.Shift.BEFORE))
    public void onTickHook(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new UpdateEvent());
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V",
                    shift = At.Shift.BEFORE))
    public void sendMovementPackets_Pre(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new PreMotionUpdateEvent());
        if (ROTATION_BYPASS.isEnabled())
        {
            motionEvent = new MotionUpdateEvent(Stage.PRE,
                    this.pos.x,
                    this.getBoundingBox().minY,
                    this.pos.z,
                    this.yaw,
                    this.pitch,
                    this.onGround);
            // if (!PingBypass.isConnected())
            // {
            Bus.EVENT_BUS.post(motionEvent);
            pos.x = motionEvent.getX();
            pos.y = motionEvent.getY();
            pos.z = motionEvent.getZ();
            yaw = motionEvent.getRotationYaw();
            pitch = motionEvent.getRotationPitch();
            onGround = motionEvent.isOnGround();
            // }
        }
    }

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void sendMovementPackets_Head(CallbackInfo callbackInfo)
    {
        if (!ROTATION_BYPASS.isEnabled())
        {
            motionEvent = new MotionUpdateEvent(Stage.PRE,
                    this.pos.x,
                    this.getBoundingBox().minY,
                    this.pos.z,
                    this.yaw,
                    this.pitch,
                    this.onGround);
            // if (!PingBypass.isConnected())
            // {
            Bus.EVENT_BUS.post(motionEvent);
            // }
        }

        if (motionEvent.isCancelled())
        {
            callbackInfo.cancel();
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V",
                    shift = At.Shift.AFTER))
    public void sendMovementPackets_Post(CallbackInfo ci)
    {
        if (ROTATION_BYPASS.isEnabled() && !ROTATION_BYPASS.returnIfPresent(
                Compatibility::isShowingRotations, false)
                /*&& !PingBypass.isConnected()*/)
        {
            // maybe someone else changed our position in the meantime
            if (pos.x == motionEvent.getX())
            {
                pos.x = motionEvent.getInitialX();
            }

            if (pos.y == motionEvent.getY())
            {
                pos.y = motionEvent.getInitialY();
            }

            if (pos.z == motionEvent.getZ())
            {
                pos.z = motionEvent.getInitialZ();
            }

            if (yaw == motionEvent.getRotationYaw())
            {
                yaw = motionEvent.getInitialYaw();
            }

            if (pitch == motionEvent.getRotationPitch())
            {
                pitch = motionEvent.getInitialPitch();
            }

            if (onGround == motionEvent.isOnGround())
            {
                onGround = motionEvent.isInitialOnGround();
            }
        }
    }
    @Redirect(
            method = "sendMovementPackets",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Vec3d;x:D"))
    public double posXHook(Vec3d vector)
    {
        return motionEvent.getX();
    }

    // @Redirect(
    //         method = "sendMovementPackets",
    //         at = @At(
    //                 value = "FIELD",
    //                 target = "Lnet/minecraft/util/math/Vec3d;y:D"))
    // public double minYHook(Vec3d vector)
    // {
    //     return motionEvent.getY();
    // }

    @Redirect(
            method = "sendMovementPackets",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Vec3d;z:D"))
    public double posZHook(Vec3d vector)
    {
        return motionEvent.getZ();
    }

    // @Redirect(
    //         method = "sendMovementPackets",
    //         at = @At(
    //                 value = "FIELD",
    //                 target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastYaw:F"))
    // public float rotationYawHook(ClientPlayerEntity entity)
    // {
    //     return motionEvent.getYaw();
    // }

    // @Redirect(
    //         method = "sendMovementPackets",
    //         at = @At(
    //                 value = "FIELD",
    //                 target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastPitch:F"))
    // public float rotationPitchHook(ClientPlayerEntity clientPlayerEntity)
    // {
    //     return motionEvent.getPitch();
    // }

    // @Redirect(
    //         method = "sendMovementPackets",
    //         at = @At(
    //                 value = "FIELD",
    //                 target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
    // public boolean onGroundHook(ClientPlayerEntity clientPlayerEntity)
    // {
    //     return motionEvent.isOnGround();
    // }

    @Inject(
            method = "sendMovementPackets",
            at = @At(value = "RETURN"))
    public void onUpdateWalkingPlayer_Return(CallbackInfo callbackInfo)
    {
        // if (!PingBypass.isConnected())
        // {
        MotionUpdateEvent event = new MotionUpdateEvent(Stage.POST, motionEvent);
        event.setCancelled(motionEvent.isCancelled());
        Bus.EVENT_BUS.postReversed(event, null);
        // }
    }

    @Inject(
            method = "isCamera",
            at = @At("HEAD"),
            cancellable = true)
    public void isCurrentViewEntityHook(CallbackInfoReturnable<Boolean> cir)
    {
        if (!isSpectator() && SPECTATE.isEnabled())
        {
            cir.setReturnValue(true);
        }
    }
}
