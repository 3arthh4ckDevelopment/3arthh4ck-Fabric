package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity, Globals
{
    @Unique
    private static final ModuleCache<NoRender>
        NO_RENDER = Caches.getModule(NoRender.class);
    @Unique
    private static final ModuleCache<AutoSprint>
        SPRINT = Caches.getModule(AutoSprint.class);
    @Unique
    private static final ModuleCache<Velocity>
        VELOCITY = Caches.getModule(Velocity.class);
    @Unique
    private static final ModuleCache<NoInterp>
        NOINTERP = Caches.getModule(NoInterp.class);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Velocity>
        NO_PUSH = Caches.getSetting
            (Velocity.class, BooleanSetting.class, "NoPush", false);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, Step>
        STEP_COMP = Caches.getSetting
            (Step.class, BooleanSetting.class, "Compatibility", false);

    @Unique
    private static final SettingCache
            <Integer, NumberSetting<Integer>, Management> DEATH_TIME =
            Caches.getSetting(Management.class, Setting.class, "DeathTime", 500);

    @Shadow public Vec3d pos;
    @Shadow private Vec3d velocity;
    @Shadow public float yaw;
    @Shadow public float pitch;
    @Shadow public boolean onGround;
    @Shadow private World world;
    @Shadow public double prevX;
    @Shadow public double prevY;
    @Shadow public double prevZ;
    @Shadow public double lastRenderX;
    @Shadow public double lastRenderY;
    @Shadow public double lastRenderZ;
    @Shadow @Final protected DataTracker dataTracker;
    @Shadow private Entity.RemovalReason removalReason;
    @Shadow private EntityDimensions dimensions;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;
    @Unique private long oldServerX;
    @Unique private long oldServerY;
    @Unique private long oldServerZ;
    @Unique private final StopWatch pseudoWatch = new StopWatch();
    @Unique private MoveEvent moveEvent;
    @Unique private float prevHeight;
    @Unique private Supplier<EntityType> type;
    @Unique private boolean pseudoDead;
    @Unique private long stamp;
    @Unique private boolean dummy;
    @Shadow public abstract Box getBoundingBox();
    @Shadow public abstract boolean isSneaking();
    @Shadow protected abstract boolean getFlag(int flag);
    @Shadow public abstract boolean equals(Object p_equals_1_);
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow public abstract boolean hasVehicle();
    @Shadow public abstract void move(MovementType movementType, Vec3d movement);
    @Shadow public abstract Text getName();
    @Shadow public abstract int getId();
    @Shadow public abstract float getPitch();

    @Override
    public EntityType earthhack$getType()
    {
        return type.get();
    }

    @Override
    public boolean earthhack$inWeb()
    {
        // todo
        return false;
    }

    @Override
    public long earthhack$getDeathTime()
    {
        // TODO!!!
        return 0;
    }

    @Override
    public void earthhack$setOldServerPos(long x, long y, long z)
    {
        this.oldServerX = x;
        this.oldServerY = y;
        this.oldServerZ = z;
    }

    @Override
    public long earthhack$getOldServerPosX()
    {
        return oldServerX;
    }

    @Override
    public long earthhack$getOldServerPosY()
    {
        return oldServerY;
    }

    @Override
    public long earthhack$getOldServerPosZ()
    {
        return oldServerZ;
    }

    @Override
    public boolean earthhack$isPseudoDead()
    {
        if (pseudoDead
                && !removalReason.shouldDestroy()
                && pseudoWatch.passed(DEATH_TIME.getValue()))
        {
            pseudoDead = false;
        }

        return pseudoDead;
    }

    @Override
    public void earthhack$setPseudoDead(boolean pseudoDead)
    {
        this.pseudoDead = pseudoDead;
        if (pseudoDead)
        {
            pseudoWatch.reset();
        }
    }

    @Override
    public StopWatch earthhack$getPseudoTime()
    {
        return pseudoWatch;
    }

    @Override
    public long earthhack$getTimeStamp()
    {
        return stamp;
    }

    @Override
    public boolean earthhack$isDummy()
    {
        return dummy;
    }

    @Override
    public void earthhack$setDummy(boolean dummy)
    {
        this.dummy = dummy;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ctrHook(CallbackInfo info)
    {
        this.type = EntityType.getEntityType(Entity.class.cast(this));
        this.stamp = System.currentTimeMillis();
    }

    @Inject(method = "setPos", at = @At("RETURN"))
    public void setPositionAndRotationHook(double x,
                                           double y,
                                           double z,
                                           CallbackInfo ci)
    {
        if (this instanceof IEntityNoInterp)
        {
            ((IEntityNoInterp) this).earthhack$setNoInterpX(x);
            ((IEntityNoInterp) this).earthhack$setNoInterpY(y);
            ((IEntityNoInterp) this).earthhack$setNoInterpZ(z);
        }
    }

    @Inject(
            method = "spawnSprintingParticles",
            at = @At("HEAD"),
            cancellable = true)
    public void createRunningParticlesHook(CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (ClientPlayerEntity.class.isInstance(this)
                && SPRINT.isEnabled()
                && SPRINT.get().getMode() == SprintMode.Rage)
        {
            ci.cancel();
        }
    }

    @Inject(
            method = "move",
            at = @At("HEAD"),
            cancellable = true)
    public void moveEntityHook_Head(MovementType movementType, Vec3d movement, CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (ClientPlayerEntity.class.isInstance(this))
        {
            this.moveEvent = new MoveEvent(movementType, movement, this.isSneaking());
            Bus.EVENT_BUS.post(this.moveEvent);
            if (moveEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "move",
            at = @At("RETURN"))
    public void moveEntityHook_Return(MovementType type,
                                      Vec3d movement,
                                      CallbackInfo info)
    {
        this.moveEvent = null;
    }

    @ModifyVariable(
            method = "move",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private Vec3d setVec(Vec3d vec)
    {
        return this.moveEvent != null ? this.moveEvent.getVec() : vec;
    }


    @Inject(method = "kill", at = @At("RETURN"))
    public void killHook(CallbackInfo ci) {
        if (NOINTERP.isPresent() && NOINTERP.get().shouldFixDeathJitter())
        {
            removeInterpolation();
            // schedule as well in case this was called on a different thread.
            mc.execute(this::removeInterpolation);
        }
    }

    @Inject(
            method = "refreshPositionAndAngles(DDDFF)V",
            at = @At("RETURN"))
    public void refreshPositionAndAnglesHook(double x, double y,
                                             double z, float yaw,
                                             float pitch, CallbackInfo ci)
    {
        // noinspection ConstantConditions
        if (NOINTERP.isEnabled())
        {
            NoInterp.handleNoInterp(NOINTERP.get(),
                    Entity.class.cast(this),
                    x,
                    y,
                    z,
                    yaw,
                    pitch);
        }
    }

    // TODO: Fix this
    // @Redirect(
    //         method = "move",
    //         at = @At(
    //                 value = "INVOKE",
    //                 target = "net/minecraft/entity/Entity.isSneaking()Z"))
    // public boolean isSneakingHook(boolean sneaking)
    // {
    //     return this.moveEvent != null
    //             ? this.moveEvent.isSneaking()
    //             : sneaking;
    // }

//    @Inject( // not needed anymore ig, from 1.21 use mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(value);
//            method = "move",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/block/Block;onSteppedOn(Lnet/minecraft/world/World;" +
//                            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;" +
//                            "Lnet/minecraft/entity/Entity;)V"/*,
//                             ordinal = 2*/))
//    public void onGroundHook(MovementType type,
//                             Vec3d movement,
//                             CallbackInfo info)
//    {
//        //noinspection ConstantConditions
//        if (ClientPlayerEntity.class.isInstance(this) && !STEP_COMP.getValue()) {
//            StepEvent event = new StepEvent(Stage.PRE,
//                    this.getBoundingBox(),
//                    this.stepHeight);
//            Bus.EVENT_BUS.post(event);
//            this.prevHeight = this.stepHeight;
//            this.stepHeight = event.getHeight();
//        }
//    }

    // TODO: Fix this aswell
    // @Inject(
    //         method = "move",
    //         at = @At(
    //                 value = "FIELD",
    //                 target = "Lnet/minecraft/entity/Entity;stepHeight:F",
    //                 ordinal = 3,
    //                 shift = At.Shift.BEFORE))
    // public void onGroundHookComp(MovementType type,
    //                              Vec3d movement,
    //                              CallbackInfo info) {
    //     //noinspection ConstantConditions
    //     if (ClientPlayerEntity.class.isInstance(this) && STEP_COMP.getValue()) {
    //         StepEvent event = new StepEvent(Stage.PRE,
    //                 this.getBoundingBox(),
    //                 this.stepHeight);
    //         Bus.EVENT_BUS.post(event);
    //         this.prevHeight = this.stepHeight;
    //         this.stepHeight = event.getHeight();
    //     }
    // }

    @Inject(
        method = "doesRenderOnFire",
        at = @At("HEAD"),
        cancellable = true)
    public void canRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        if (NO_RENDER.isEnabled() && NO_RENDER.get().noEntityFire()) cir.setReturnValue(false);
    }

    @Redirect(
            method = "pushAwayFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z)
    {
        if (entity != null && (!VELOCITY.isEnabled()
                || !NO_PUSH.getValue()
                || !entity.equals(mc.player)))
        {
            entity.addVelocity(x, y, z);
        }
    }

    @Unique
    private void removeInterpolation()
    {
        this.prevX = this.pos.x;
        this.prevY = this.pos.y;
        this.prevZ = this.pos.z;

        this.lastRenderX = this.pos.x;
        this.lastRenderY = this.pos.y;
        this.lastRenderZ = this.pos.z;

        this.prevHeight = this.dimensions.height();

        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;

        if (this instanceof IEntityNoInterp)
        {
            ((IEntityNoInterp) this).earthhack$setNoInterpX(this.pos.x);
            ((IEntityNoInterp) this).earthhack$setNoInterpY(this.pos.y);
            ((IEntityNoInterp) this).earthhack$setNoInterpZ(this.pos.z);
        }
    }
}
