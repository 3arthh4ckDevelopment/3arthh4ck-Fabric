package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import me.earth.earthhack.impl.core.ducks.entity.ILivingEntity;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

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

    @Shadow
    @Final
    private static TrackedData<Float> HEALTH;
    @Shadow
    public float sidewaysSpeed;
    @Shadow
    public float forwardSpeed;
    // @Shadow
    // protected int activeItemStackUseCount; // TODO: I forgot this... I'll add it later ~nuk
    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect var1);

    @Shadow
    public abstract Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();

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
    public abstract int getTicksSinceLastSwing();

    @Override
    // @Accessor(value = "activeItemStackUseCount") // TODO Forgot
    public abstract int getActiveItemStackUseCount();

    @Override
    @Accessor(value = "handSwingTicks")
    public abstract void setTicksSinceLastSwing(int ticks);

    @Override
    // @Accessor(value = "activeItemStackUseCount") // TODO Forgot
    public abstract void setActiveItemStackUseCount(int count);

    @Override
    public boolean getElytraFlag()
    {
        return this.getFlag(7);
    }

    @Override
    public double getNoInterpX()
    {
        return isNoInterping() ? noInterpX : pos.x;
    }

    @Override
    public double getNoInterpY()
    {
        return isNoInterping() ? noInterpY: pos.y;
    }

    @Override
    public double getNoInterpZ()
    {
        return isNoInterping() ? noInterpZ : pos.z;
    }

    @Override
    public void setNoInterpX(double x)
    {
        this.noInterpX = x;
    }

    @Override
    public void setNoInterpY(double y)
    {
        this.noInterpY = y;
    }

    @Override
    public void setNoInterpZ(double z)
    {
        this.noInterpZ = z;
    }

    @Override
    public int getPosIncrements()
    {
        return noInterpPositionIncrements;
    }

    @Override
    public void setPosIncrements(int posIncrements)
    {
        this.noInterpPositionIncrements = posIncrements;
    }

    @Override
    public float getNoInterpSwingAmount()
    {
        return noInterpSwingAmount;
    }

    @Override
    public float getNoInterpSwing()
    {
        return noInterpSwing;
    }

    @Override
    public float getNoInterpPrevSwing()
    {
        return noInterpPrevSwing;
    }

    @Override
    public void setNoInterpSwingAmount(float noInterpSwingAmount)
    {
        this.noInterpSwingAmount = noInterpSwingAmount;
    }

    @Override
    public void setNoInterpSwing(float noInterpSwing)
    {
        this.noInterpSwing = noInterpSwing;
    }

    @Override
    public void setNoInterpPrevSwing(float noInterpPrevSwing)
    {
        this.noInterpPrevSwing = noInterpPrevSwing;
    }

    @Override
    public boolean isNoInterping()
    {
        ClientPlayerEntity player = mc.player;
        return !this.hasVehicle()
                && noInterping
                && (player == null || !player.isRiding());
    }

    @Override
    public void setNoInterping(boolean noInterping)
    {
        this.noInterping = noInterping;
    }

    @Override
    public void setLowestDura(float lowest)
    {
        this.lowestDura = lowest;
    }

    @Override
    public float getLowestDurability()
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

    @Override
    public int getExplosionModifier(DamageSource source)
    {
        return shouldCache()
                ? explosionModifier
                : EnchantmentUtil.getEnchantmentModifierDamage(
                this.getArmorItems(), source);
    }
}
