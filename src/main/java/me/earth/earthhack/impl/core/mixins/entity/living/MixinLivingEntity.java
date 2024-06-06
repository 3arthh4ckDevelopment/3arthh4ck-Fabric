package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import me.earth.earthhack.impl.core.ducks.entity.ILivingEntity;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.event.events.render.SuffocationEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends MixinEntity
        implements ILivingEntity, IEntityNoInterp,
        ICachedDamage, IEntityRemoteAttack
{
    // private static final ModuleCache<ElytraFlight> ELYTRA_FLIGHT =
    //         Caches.getModule(ElytraFlight.class);
    @Unique
    private static final ModuleCache<FastEat> FAST_EAT =
            Caches.getModule(FastEat.class);
    @Unique
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);
    @Unique
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);

    // private static final ModuleCache<Swing> SWING =
    //         Caches.getModule(Swing.class);
    @Unique
    private static final ModuleCache<NoRender> NO_RENDER =
            Caches.getModule(NoRender.class);
    @Unique
    private static final ModuleCache<AutoSprint> SPRINT =
            Caches.getModule(AutoSprint.class);

    @Shadow public float sidewaysSpeed;
    @Shadow public float forwardSpeed;
    // @Shadow
    // protected int activeItemStackUseCount; // TODO: I forgot this... I'll add it later ~nuk
    @Shadow protected ItemStack activeItemStack;
    @Shadow public abstract boolean hasStatusEffect(StatusEffect var1);
    @Shadow public abstract Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();

    /* Unique fields. */
    @Unique protected double noInterpX;
    @Unique protected double noInterpY;
    @Unique protected double noInterpZ;
    @Unique protected int noInterpPositionIncrements;
    @Unique protected float noInterpPrevSwing;
    @Unique protected float noInterpSwingAmount;
    @Unique protected float noInterpSwing;
    @Unique protected float lowestDura = Float.MAX_VALUE;
    @Unique protected boolean noInterping = true;

    @Unique protected int armorValue = Integer.MAX_VALUE;
    @Unique protected float armorToughness = Float.MAX_VALUE;
    @Unique protected int explosionModifier = Integer.MAX_VALUE;

    @Shadow
    public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Shadow
    public abstract int getArmor();

    @Shadow
    public abstract Iterable<ItemStack> getArmorItems();

    // NOTICE: use LivingEntity#getWorld()#isClient() instead

    // @Shadow
    // public abstract boolean isServerWorld();


    // @Shadow @Final
    // private static Logger LOGGER;

    @Shadow public float headYaw;

    @Override
    @Accessor(value = "handSwingTicks")
    public abstract int earthhack$getTicksSinceLastSwing();

    @Override
    // @Accessor(value = "activeItemStackUseCount") // TODO Forgot
    public abstract int earthhack$getActiveItemStackUseCount();

    @Override
    @Accessor(value = "handSwingTicks")
    public abstract void earthhack$setTicksSinceLastSwing(int ticks);

    @Override
    // @Accessor(value = "activeItemStackUseCount") // TODO Forgot
    public abstract void earthhack$setActiveItemStackUseCount(int count);

    @Override
    public boolean earthhack$getElytraFlag()
    {
        return this.getFlag(7);
    }

    @Override
    public double earthhack$getNoInterpX()
    {
        return earthhack$isNoInterping() ? noInterpX : pos.x;
    }

    @Override
    public double earthhack$getNoInterpY()
    {
        return earthhack$isNoInterping() ? noInterpY: pos.y;
    }

    @Override
    public double earthhack$getNoInterpZ()
    {
        return earthhack$isNoInterping() ? noInterpZ : pos.z;
    }

    @Override
    public void earthhack$setNoInterpX(double x)
    {
        this.noInterpX = x;
    }

    @Override
    public void earthhack$setNoInterpY(double y)
    {
        this.noInterpY = y;
    }

    @Override
    public void earthhack$setNoInterpZ(double z)
    {
        this.noInterpZ = z;
    }

    @Override
    public int earthhack$getPosIncrements()
    {
        return noInterpPositionIncrements;
    }

    @Override
    public void earthhack$setPosIncrements(int posIncrements)
    {
        this.noInterpPositionIncrements = posIncrements;
    }

    @Override
    public float earthhack$getNoInterpSwingAmount()
    {
        return noInterpSwingAmount;
    }

    @Override
    public float earthhack$getNoInterpSwing()
    {
        return noInterpSwing;
    }

    @Override
    public float earthhack$getNoInterpPrevSwing()
    {
        return noInterpPrevSwing;
    }

    @Override
    public void earthhack$setNoInterpSwingAmount(float noInterpSwingAmount)
    {
        this.noInterpSwingAmount = noInterpSwingAmount;
    }

    @Override
    public void earthhack$setNoInterpSwing(float noInterpSwing)
    {
        this.noInterpSwing = noInterpSwing;
    }

    @Override
    public void earthhack$setNoInterpPrevSwing(float noInterpPrevSwing)
    {
        this.noInterpPrevSwing = noInterpPrevSwing;
    }

    @Override
    public boolean earthhack$isNoInterping()
    {
        ClientPlayerEntity player = mc.player;
        return !this.hasVehicle()
                && noInterping
                && (player == null || !player.isRiding());
    }

    @Override
    public void earthhack$setNoInterping(boolean noInterping)
    {
        this.noInterping = noInterping;
    }

    @Override
    public void earthhack$setLowestDura(float lowest)
    {
        this.lowestDura = lowest;
    }

    @Override
    public float earthhack$getLowestDurability()
    {
        return lowestDura;
    }

    @Override
    public int getArmorValue()
    {
        return shouldCache()
                ? armorValue
                : this.getArmor();
    }

    @Override
    public float getArmorToughness()
    {
        return shouldCache()
                ? armorToughness
                : (float) this
                .getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
                .getValue();
    }

    @Inject(
            method = "setHealth",
            at = @At("RETURN"))
    public void setHealthHook(float health, CallbackInfo info)
    {
        if (health <= 0.0
                && LivingEntity.class.cast(this).getWorld() != null
                && !LivingEntity.class.cast(this).getWorld().isClient)
        {
            Bus.EVENT_BUS.post(new DeathEvent(
                    LivingEntity.class.cast(this)));
        }
    }

    @Override
    public int getExplosionModifier(DamageSource source)
    {
        return shouldCache()
                ? explosionModifier
                : EnchantmentUtil.getEnchantmentModifierDamage(
                this.getArmorItems(), source);
    }

    @Inject(
        method = "isInsideWall",
        at = @At(value="HEAD"),
        cancellable = true)
    public void isEntityInsideOpaqueBlockHook(
            CallbackInfoReturnable<Boolean> info)
    {
        SuffocationEvent event = new SuffocationEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @ModifyVariable(
            method = "spawnConsumptionEffects",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    public int updateItemUse(int eatingParticlesAmount) {
        if (NO_RENDER.isEnabled() && NO_RENDER.get().noEatingParticles())
            return 0;
        else return eatingParticlesAmount;
    }


    /**
     * Refactored from MixinClientPlayerEntity to here because this doesn't work from these correctly
     */
    @ModifyArg(
            method = "setSprinting",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setSprinting(Z)V"))
    public boolean setSprintingHook(boolean sprinting)
    {
        if (SPRINT.isEnabled() && AutoSprint.canSprintBetter() && (SPRINT.get().getMode() == SprintMode.Rage && MovementUtil.isMoving()))
        {
            return true;
        }

        return sprinting;
    }
}
