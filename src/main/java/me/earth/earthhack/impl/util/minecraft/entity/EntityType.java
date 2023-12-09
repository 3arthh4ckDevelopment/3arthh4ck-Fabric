package me.earth.earthhack.impl.util.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;

import java.awt.*;
import java.util.function.Supplier;

public enum EntityType
{
    Animal(new Color(0, 200, 0, 255)),
    Monster(new Color(200, 60, 60, 255)),
    Player(new Color(255, 255, 255, 255)),
    Boss(new Color(40, 0, 255, 255)),
    Vehicle(new Color(200, 100, 0, 255)),
    Other(new Color(200, 100, 200, 255)),
    Entity(new Color(255, 255, 0, 255));

    private final Color color;

    EntityType(Color color)
    {
        this.color = color;
    }

    /** @return the Color belonging to the given EntityType. */
    public Color getColor()
    {
        return color;
    }

    /*------------------- Static Util -------------------*/
    public static Supplier<EntityType> getEntityType(net.minecraft.entity.Entity entity)
    {
        if (entity instanceof WolfEntity)
        {
            return () -> isAngryWolf((WolfEntity) entity)
                    ? Monster
                    : Animal;
        }

        if (entity instanceof EndermanEntity)
        {
            return () -> isAngryEnderMan((EndermanEntity) entity)
                    ? Monster
                    : Entity;
        }

        if (entity instanceof PolarBearEntity)
        {
            return () -> isAngryPolarBear((PolarBearEntity) entity)
                    ? Monster
                    : Animal;
        }

        if (entity instanceof ZombifiedPiglinEntity)
        {
            return () -> entity.getPitch() == 0.0F
                    && ((ZombifiedPiglinEntity) entity).getAngerTime() <= 0
                    ? Monster
                    : Entity;
        }

        if (entity instanceof IronGolemEntity)
        {
            return () -> isAngryGolem((IronGolemEntity) entity)
                    ? Monster
                    : Entity;
        }

        if (entity instanceof VillagerEntity)
        {
            return () -> Entity;
        }

        if (entity instanceof RabbitEntity)
        {
            return () -> isFriendlyRabbit((RabbitEntity) entity)
                    ? Animal
                    : Monster;
        }

        if (isAnimal(entity))
        {
            return () -> Animal;
        }

        if (isMonster(entity))
        {
            return () -> Monster;
        }

        if (isPlayer(entity))
        {
            return () -> Player;
        }

        if (isVehicle(entity))
        {
            return () -> Vehicle;
        }

        if (isBoss(entity))
        {
            return () -> Boss;
        }

        if (isOther(entity))
        {
            return () -> Other;
        }

        return () -> Entity;
    }

    public static boolean isPlayer(Entity entity)
    {
        return entity instanceof PlayerEntity;
    }

    public static boolean isAnimal(Entity entity)
    {
        return entity instanceof PigEntity
                || entity instanceof ParrotEntity
                || entity instanceof CowEntity
                || entity instanceof SheepEntity
                || entity instanceof ChickenEntity
                || entity instanceof SquidEntity
                || entity instanceof BatEntity
                || entity instanceof VillagerEntity
                || entity instanceof OcelotEntity
                || entity instanceof HorseEntity
                || entity instanceof LlamaEntity
                || entity instanceof MuleEntity
                || entity instanceof DonkeyEntity
                || entity instanceof SkeletonHorseEntity
                || entity instanceof ZombieHorseEntity
                || entity instanceof SnowGolemEntity
                || entity instanceof RabbitEntity
                && isFriendlyRabbit((RabbitEntity) entity);
    }

    public static boolean isMonster(Entity entity)
    {
        return entity instanceof CreeperEntity
                || entity instanceof IllusionerEntity
                || entity instanceof SkeletonEntity
                || entity instanceof ZombieEntity
                && !(entity instanceof ZombifiedPiglinEntity)
                || entity instanceof BlazeEntity
                || entity instanceof SpiderEntity
                || entity instanceof WitchEntity
                || entity instanceof SlimeEntity
                || entity instanceof SilverfishEntity
                || entity instanceof GuardianEntity
                || entity instanceof EndermiteEntity
                || entity instanceof GhastEntity
                || entity instanceof EvokerEntity
                || entity instanceof ShulkerEntity
                || entity instanceof WitherSkeletonEntity
                || entity instanceof StrayEntity
                || entity instanceof VexEntity
                || entity instanceof VindicatorEntity
                || entity instanceof PolarBearEntity
                && !isAngryPolarBear((PolarBearEntity) entity)
                || entity instanceof WolfEntity
                && !isAngryWolf((WolfEntity) entity)
                || entity instanceof ZombifiedPiglinEntity
                && !isAngryPigMan(entity)
                || entity instanceof EndermanEntity
                && !isAngryEnderMan((EndermanEntity) entity)
                || entity instanceof RabbitEntity
                && !isFriendlyRabbit((RabbitEntity) entity)
                || entity instanceof IronGolemEntity
                && !isAngryGolem((IronGolemEntity) entity);
    }

    public static boolean isBoss(Entity entity)
    {
        return entity instanceof EnderDragonEntity
                || entity instanceof WitherEntity
                || entity instanceof GiantEntity;
    }

    public static boolean isOther(Entity entity)
    {
        return entity instanceof EndCrystalEntity
                || entity instanceof EvokerFangsEntity
                || entity instanceof ShulkerBulletEntity
                || entity instanceof FallingBlockEntity
                || entity instanceof FireballEntity
                || entity instanceof EyeOfEnderEntity
                || entity instanceof EnderPearlEntity;
    }

    public static boolean isVehicle(Entity entity)
    {
        return entity instanceof BoatEntity || entity instanceof MinecartEntity;
    }

    public static boolean isAngryEnderMan(EndermanEntity enderman)
    {
        return enderman.isProvoked();
    }

    public static boolean isAngryPigMan(Entity entity)
    {
        return entity instanceof ZombifiedPiglinEntity
                && entity.getPitch() == 0.0F
                && ((ZombifiedPiglinEntity) entity).getAngerTime() <= 0;
    }

    public static boolean isAngryGolem(IronGolemEntity ironGolem)
    {
        return ironGolem.getPitch() == 0.0F;
    }

    public static boolean isAngryWolf(WolfEntity wolf)
    {
        return wolf.isAttacking();
    }

    public static boolean isAngryPolarBear(PolarBearEntity polarBear)
    {
        return polarBear.getPitch() == 0.0f
                && polarBear.getAngerTime() <= 0;
    }

    public static boolean isFriendlyRabbit(RabbitEntity rabbit)
    {
        return rabbit.getVariant() != RabbitEntity.RabbitType.EVIL;
    }

}
