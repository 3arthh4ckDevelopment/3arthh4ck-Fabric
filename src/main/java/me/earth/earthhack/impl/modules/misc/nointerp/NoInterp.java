package me.earth.earthhack.impl.modules.misc.nointerp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class NoInterp extends Module {

    private final Setting<Boolean> silent =
            register(new BooleanSetting("Silent", true));
    private final Setting<Boolean> setRotations =
            register(new BooleanSetting("Fast-Rotations", false));
    private final Setting<Boolean> noDeathJitter =
            register(new BooleanSetting("NoDeathJitter", true));
    // TODO: THIS, problem with Jockeys... (fix now)
    private final Setting<Boolean> onlyPlayers =
            register(new BooleanSetting("OnlyPlayers", false));

    public NoInterp() {
        super("NoInterp", Category.Misc);
        this.setData(new SimpleData(this, "Makes the client more accurate."));
    }

    public boolean isSilent()
    {
        return silent.getValue();
    }

    public boolean shouldFixDeathJitter()
    {
        return noDeathJitter.getValue();
    }

    public boolean isOnlyPlayers() { return onlyPlayers.getValue(); }

    public static void handleNoInterp(NoInterp noInterp,
                                      Entity entity,
                                      double x,
                                      double y,
                                      double z,
                                      float yaw,
                                      float pitch)
    {
        IEntityNoInterp entityNoInterp = (IEntityNoInterp) entity;
        if (!entityNoInterp.earthhack$isNoInterping())
        {
            return;
        }

        if(noInterp.isOnlyPlayers() && !(entity instanceof PlayerEntity)) return;

        if (noInterp.setRotations.getValue())
        {
            entity.setPosition(x, y, z);
            entity.setHeadYaw(yaw % 360.0f);
            entity.yaw = yaw % 360.0f;
            entity.pitch = pitch % 360.0f;
        }
        else
        {
            entity.setPosition(x, y, z);
        }
    }

    public static double noInterpX(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            return ((IEntityNoInterp) entity).earthhack$getNoInterpX();
        }

        return entity.getX();
    }

    public static double noInterpY(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            return ((IEntityNoInterp) entity).earthhack$getNoInterpY();
        }

        return entity.getY();
    }

    public static double noInterpZ(NoInterp noInterp, Entity entity)
    {
        if (noInterp != null
                && noInterp.isEnabled()
                && noInterp.isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            return ((IEntityNoInterp) entity).earthhack$getNoInterpZ();
        }

        return entity.getZ();
    }

    public static boolean update(NoInterp module, Entity entity)
    {
        if (module == null
                || !module.isEnabled()
                || !module.silent.getValue()
                || EntityUtil.isDead(entity))
        {
            return false;
        }

        IEntityNoInterp noInterp;
        if (!(entity instanceof IEntityNoInterp)
                || !(noInterp = (IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            return false;
        }

        if (noInterp.earthhack$getPosIncrements() > 0)
        {
            double x = noInterp.earthhack$getNoInterpX()
                    + (entity.getX() - noInterp.earthhack$getNoInterpX())
                    / (double) noInterp.earthhack$getPosIncrements();
            double y = noInterp.earthhack$getNoInterpY()
                    + (entity.getY() - noInterp.earthhack$getNoInterpY())
                    / (double) noInterp.earthhack$getPosIncrements();
            double z = noInterp.earthhack$getNoInterpZ()
                    + (entity.getZ() - noInterp.earthhack$getNoInterpZ())
                    / (double) noInterp.earthhack$getPosIncrements();

            entity.prevX = noInterp.earthhack$getNoInterpX();
            entity.prevY = noInterp.earthhack$getNoInterpY();
            entity.prevZ = noInterp.earthhack$getNoInterpZ();

            entity.lastRenderX = noInterp.earthhack$getNoInterpX();
            entity.lastRenderY = noInterp.earthhack$getNoInterpY();
            entity.lastRenderZ = noInterp.earthhack$getNoInterpZ();

            noInterp.earthhack$setNoInterpX(x);
            noInterp.earthhack$setNoInterpY(y);
            noInterp.earthhack$setNoInterpZ(z);

            noInterp.earthhack$setPosIncrements(noInterp.earthhack$getPosIncrements() - 1);
        }

        if (entity instanceof LivingEntity base)
        {
            double xDiff = noInterp.earthhack$getNoInterpX() - entity.prevX;
            double zDiff = noInterp.earthhack$getNoInterpZ() - entity.prevZ;
            double yDiff = entity instanceof FlyingEntity
                    ? noInterp.earthhack$getNoInterpY() - entity.prevY
                    : 0.0;

            float diff = MathHelper.sqrt((float) (xDiff * xDiff
                    + zDiff * zDiff
                    + yDiff * yDiff)) * 4.0f;
            if (diff > 1.0f)
            {
                diff = 1.0f;
            }

            float limbSwingAmount = noInterp.earthhack$getNoInterpSwingAmount();
            base.handSwingProgress = limbSwingAmount;
            noInterp.earthhack$setNoInterpPrevSwing(limbSwingAmount);
            noInterp.earthhack$setNoInterpSwingAmount(limbSwingAmount
                    + (diff - limbSwingAmount) * 0.4f);
            base.handSwingProgress = noInterp.earthhack$getNoInterpSwingAmount();
            float limbSwing = noInterp.earthhack$getNoInterpSwing()
                    + base.handSwingProgress;
            noInterp.earthhack$setNoInterpSwing(limbSwing);
            base.handSwingProgress = limbSwing;
        }

        return true;
    }
}
