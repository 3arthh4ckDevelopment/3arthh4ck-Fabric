package me.earth.earthhack.impl.modules.combat.killaura;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraSwitch;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTarget;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTeleport;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class KillAura extends EntityTypeModule
{
    protected final Setting<Boolean> passengers =
        register(new BooleanSetting("Passengers", false));
    protected final Setting<AuraTarget> targetMode =
        register(new EnumSetting<>("Target", AuraTarget.Closest));
    protected final Setting<Boolean> prioEnemies =
        register(new BooleanSetting("Enemies", true));
    protected final Setting<Double> range =
        register(new NumberSetting<>("Range", 6.0, 0.0, 7.0));
    protected final Setting<Double> wallRange =
        register(new NumberSetting<>("WallRange", 3.0, 0.0, 7.0));
    protected final Setting<Boolean> swordOnly =
        register(new BooleanSetting("Sword/Axe", true));
    protected final Setting<Boolean> delay =
        register(new BooleanSetting("Delay", true));
    protected final Setting<Float> cps =
        register(new NumberSetting<>("CPS", 20.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> stopSneak  =
        register(new BooleanSetting("Release-Sneak", true));
    protected final Setting<Boolean> stopSprint =
        register(new BooleanSetting("Release-Sprint", true));
    protected final Setting<Boolean> stopShield =
        register(new BooleanSetting("AutoBlock", true));
    protected final Setting<Boolean> whileEating =
        register(new BooleanSetting("While-Eating", true));
    protected final Setting<Boolean> stay =
        register(new BooleanSetting("Stay", false));
    protected final Setting<Float> soft =
        register(new NumberSetting<>("Soft", 180.0f, 0.1f, 180.0f));
    protected final Setting<Integer> rotationTicks =
        register(new NumberSetting<>("Rotation-Ticks", 0, 0, 10));
    protected final Setting<AuraTeleport> auraTeleport =
        register(new EnumSetting<>("Teleport", AuraTeleport.None));
    protected final Setting<Double> teleportRange =
        register(new NumberSetting<>("TP-Range", 0.0, 0.0, 100.0));
    protected final Setting<Boolean> tpSetPos =
        register(new BooleanSetting("TP-SetPos", false));
    protected final Setting<Boolean> yTeleport  =
        register(new BooleanSetting("Y-Teleport", false));
    protected final Setting<Boolean> movingTeleport  =
        register(new BooleanSetting("Move-Teleport", false));
    protected final Setting<Swing> swing =
        register(new EnumSetting<>("Swing", Swing.Full));
    protected final Setting<Boolean> tps =
        register(new BooleanSetting("TPS-Sync", true));
    protected final Setting<Boolean> t2k =
        register(new BooleanSetting("Fast-32ks", true));
    protected final Setting<Float> health =
        register(new NumberSetting<>("Health", 0.0f, 0.0f, 15.0f));
    protected final Setting<Integer> armor =
        register(new NumberSetting<>("Armor", 0, 0, 100));
    protected final Setting<Float> targetRange =
        register(new NumberSetting<>("Target-Range", 10.0f, 0.0f, 120.0f));
    protected final Setting<Boolean> multi32k =
        register(new BooleanSetting("Multi-32k", false));
    protected final Setting<Integer> packets =
        register(new NumberSetting<>("Packets", 1, 0, 20));
    protected final Setting<Double> height =
        register(new NumberSetting<>("Height", 1.0, 0.0, 1.0));
    protected final Setting<Boolean> ridingTeleports =
        register(new BooleanSetting("Riding-Teleports", false));
    protected final Setting<Boolean> efficient =
        register(new BooleanSetting("Efficient", false));
    protected final Setting<Boolean> cancelEntityEquip =
        register(new BooleanSetting("NoEntityEquipment", false));
    protected final Setting<Boolean> tpInfo =
        register(new BooleanSetting("TP-Info", false));
    protected final Setting<Integer> coolDown =
        register(new NumberSetting<>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> m1Attack =
        register(new BooleanSetting("Hold-Mouse", false));
    protected final Setting<AuraSwitch> autoSwitch =
        register(new EnumSetting<>("AutoSwitch", AuraSwitch.None));

    protected final RotationSmoother rotationSmoother =
            new RotationSmoother(Managers.ROTATION);
    protected final DiscreteTimer timer =
            new GuardTimer();

    protected final StopWatch targetTimer = new StopWatch();

    protected boolean isTeleporting;
    protected boolean isAttacking;
    protected boolean ourCrit;
    protected Entity target;
    protected Vec3d eff;
    protected Vec3d pos;
    protected int slot;

    public KillAura()
    {
        super("KillAura", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRiding(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerEntityEquipment(this));
        this.setData(new KillAuraData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        if (target == null || EntityUtil.isDead(target))
        {
            return null;
        }

        double distance = mc.player.squaredDistanceTo(target);
        if (distance > MathUtil.square(targetRange.getValue())
            || !shouldAttack()
                && (!tpInfo.getValue()
                     || teleportRange.getValue() == 0.0
                     || auraTeleport.getValue() != AuraTeleport.Smart))
        {
            return null;
        }

        StringBuilder name = new StringBuilder(EntityNames.getName(target))
                                    .append(TextColor.GRAY)
                                    .append(", ");
        if (distance >= 36.0)
        {
            name.append(TextColor.RED);
        }
        else if (!RotationUtil.getRotationPlayer()
                              .canSee(target) && distance >= 9.0)
        {
            if (target instanceof PlayerEntity
                    && ((PlayerEntity) target).canSee(
                            RotationUtil.getRotationPlayer()))
            {
                name.append(TextColor.WHITE);
            }
            else
            {
                name.append(TextColor.GOLD);
            }
        }
        else
        {
            name.append(TextColor.GREEN);
        }

        return name.append(MathUtil.round(Math.sqrt(distance), 2)).toString();
    }

    @Override
    public boolean isValid(Entity entity)
    {
        if (entity == null
                || mc.player.squaredDistanceTo(entity) > MathUtil.square(targetRange.getValue())
                || EntityUtil.isDead(entity)
                || entity.equals(mc.player)
                || entity.equals(mc.player.getVehicle())
                || entity instanceof PlayerEntity
                && Managers.FRIENDS.contains((PlayerEntity) entity)
                || !passengers.getValue()
                && mc.player.getPassengerList().contains(entity)
                || entity instanceof ExperienceBottleEntity
                || entity instanceof ItemEntity
                || entity instanceof ArrowEntity
                || entity instanceof EndCrystalEntity)
        {
            return false;
        }

        this.targetTimer.reset();
        return super.isValid(entity);
    }

    public Entity getTarget()
    {
        if (targetTimer.passed(1000))
        {
            target = null;
        }
        return target;
    }

    public PlayerEntity getPlayerTarget()
    {
        if (targetTimer.passed(1000))
            target = null;

        if(target instanceof PlayerEntity)
            return (PlayerEntity) target;

        return null;
    }
    protected Entity findTarget()
    {
        // TODO: make this better!
        Entity closest = null;
        Entity bestEnemy = null;

        double bestAngle = 360.0;
        float lowest = Float.MAX_VALUE;

        double distance = Double.MAX_VALUE;
        double closestEnemy = Double.MAX_VALUE;

        for (Entity entity : mc.world.getEntities())
        {
            if (!isValid(entity))
            {
                continue;
            }

            double dist = mc.player.squaredDistanceTo(entity);
            if (targetMode.getValue() == AuraTarget.Angle)
            {
                double angle = RotationUtil.getAngle(entity, 1.75);
                if (angle < bestAngle
                        && Math.sqrt(dist) - teleportRange.getValue() < 6.0)
                {
                    closest = entity;
                    bestAngle = angle;
                }

                continue;
            }

            if (prioEnemies.getValue()
                && entity instanceof PlayerEntity
                && Managers.ENEMIES.contains((PlayerEntity) entity)
                && dist < closestEnemy)
            {
                bestEnemy = entity;
                closestEnemy = dist;
            }

            if (isInRange(RotationUtil.getRotationPlayer(), entity))
            {
                if (health.getValue() != 0.0f
                        && entity instanceof LivingEntity)
                {
                    float h = EntityUtil.getHealth((LivingEntity) entity);
                    if (h < health.getValue() && h < lowest)
                    {
                        closest = entity;
                        distance = dist;
                        lowest = h;
                    }
                }

                if (armor.getValue() != 0)
                {
                    for (ItemStack stack : entity.getControllingPassenger().getAllArmorItems())
                    {
                        if (!(stack.getItem() instanceof ElytraItem)
                             && DamageUtil.getPercent(stack) < armor.getValue())
                        {
                            closest = entity;
                            distance = dist;
                            break;
                        }
                    }
                }
            }

            if (closest == null)
            {
                closest = entity;
                distance = dist;
                continue;
            }

            if (dist < distance)
            {
                closest = entity;
                distance = dist;
            }
        }

        return bestEnemy != null ? bestEnemy : closest;
    }

    public boolean isInRange(Entity from, Entity target)
    {
        return isInRange(from.getPos(), target);
    }

    public boolean isInRange(Vec3d from, Entity target)
    {
        double distance = from.squaredDistanceTo(target.getPos());
        if (distance >= MathUtil.square(range.getValue()))
        {
            return false;
        }

        if (distance < MathUtil.square(wallRange.getValue()))
        {
            return true;
        }

        // TODO:
        // return mc.world.raycastBlock(
        //         new Vec3d(from.x,
        //                   from.y + mc.player.getEyeHeight(mc.player.getPose()),
        //                   from.z),
        //         new Vec3d(target.getX(),
        //                   target.getY() + target.getEyeHeight(target.getPose()),
        //                   target.getZ()),
        //         false,
        //         false,
        //         false) == null;
        return false;
    }

    protected boolean shouldAttack()
    {
        if (m1Attack.getValue() /*&& !Mouse.rightButtonClicked*/)
        {
            return false;
        }

        return !swordOnly.getValue()
            || mc.player.getMainHandStack().getItem() instanceof SwordItem
            || mc.player.getMainHandStack().getItem() instanceof AxeItem;
    }

    protected void releaseShield()
    {
        if (mc.player.getOffHandStack().getItem() instanceof ShieldItem)
        {
            NetworkUtil.send(
                    new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                            mc.player.getBlockPos(),
                            Direction.fromVector(
                                    (int) Managers.POSITION.getX(),
                                    (int) Managers.POSITION.getY(),
                                    (int) Managers.POSITION.getZ())));
        }
    }

    protected void useShield()
    {
        // TODO
        // if ((mc.player.getMainHandStack().getItem() instanceof SwordItem
        //       || mc.player.getMainHandStack().getItem() instanceof AxeItem)
        //       && mc.player.getOffHandStack().getItem() instanceof ShieldItem)
        // {
        //     Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        //         mc.interactionManager
        //                 .rightClickBlock(mc.player, mc.world, Hand.OFF_HAND));
        // }
    }

    public Vec3d criticalCallback(Vec3d vec3d)
    {
        if (this.isEnabled() && ourCrit)
        {
            if (eff != null)
            {
                return eff;
            }

            switch (auraTeleport.getValue())
            {
                case Smart:
                    if (isTeleporting && pos != null)
                    {
                        return pos;
                    }
                    break;
                case Full:
                    return Managers.POSITION.getVec();
            }
        }

        return vec3d;
    }

}
