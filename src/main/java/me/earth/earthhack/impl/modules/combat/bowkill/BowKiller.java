package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.killaura.util.AuraTarget;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.minecraft.entity.module.EntityTypeModule;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.Objects;

public class BowKiller extends EntityTypeModule {
    protected final Setting<AuraTarget> targetMode =
            register(new EnumSetting<>("Target", AuraTarget.Closest));
    protected final Setting<Boolean> cancelRotate =
            register(new BooleanSetting("CancelRotate", false));
    protected final Setting<Boolean> move =
            register(new BooleanSetting("Move", false));
    protected final Setting<Boolean> blink =
            register(new BooleanSetting("Blink", true));
    protected final Setting<Boolean> staticS =
            register(new BooleanSetting("Static", true));
    protected final Setting<Boolean> always =
            register(new BooleanSetting("Always", false));
    protected final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", true));
    protected final Setting<Boolean> prioEnemies =
            register(new BooleanSetting("Prio-Enemies", true));
    protected final Setting<Boolean> silent =
            register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> visCheck =
            register(new BooleanSetting("VisCheck", true));
    protected final Setting<Boolean> oppSpotted =
            register(new BooleanSetting("Opp-Spotted", false));
    protected final Setting<Integer> runs =
            register(new NumberSetting<>("Runs", 8, 1, 200));
    protected final Setting<Integer> buffer =
            register(new NumberSetting<>("Buffer", 10, 0, 200));
    protected final Setting<Integer> teleports =
            register(new NumberSetting<>("Teleports", 0, 0, 100));
    protected final Setting<Integer> interval =
            register(new NumberSetting<>("Interval", 25, 0, 100));
    protected final Setting<Double> range =
            register(new NumberSetting<>("Range", 15.0, 0.0, 30.0));
    protected final Setting<Double> wallRange =
            register(new NumberSetting<>("WallRange", 10.0, 0.0, 30.0));
    protected final Setting<Float> targetRange =
            register(new NumberSetting<>("Target-Range", 30.0f, 0.0f, 50.0f));
    protected final Setting<Double> height =
            register(new NumberSetting<>("Height", 1.0, 0.0, 1.0));
    protected final Setting<Float> soft =
            register(new NumberSetting<>("Soft", 180.0f, 0.1f, 180.0f));
    protected final Setting<Integer> armor =
            register(new NumberSetting<>("Armor", 0, 0, 100));
    protected int packetsSent = 0;
    protected boolean cancelling;
    protected boolean needsMessage;
    protected boolean blockUnder = false;
    protected final RotationSmoother rotationSmoother =
            new RotationSmoother(Managers.ROTATION);
    protected Entity target;
    protected final ArrayList<EntityData> entityDataArrayList = new ArrayList<>();
    public BowKiller() {
        super("BowKiller", Category.Combat);
        this.listeners.addAll(new ListenerCPacket(this).getListeners());
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerRightClick(this));
        this.listeners.add(new ListenerStopUsingItem(this));
        this.listeners.add(new ListenerEntityChunk(this));
    }

    @Override
    public String getDisplayInfo() {
        if (cancelling) {
            if (packetsSent >= runs.getValue() * 2 || always.getValue()) {
                return TextColor.GREEN + packetsSent;
            }

            return TextColor.RED + packetsSent;
        }


        return null;
    }

    protected void onEnable() {
        packetsSent = 0;
        cancelling = false;
        needsMessage = true;
    }

    protected void onPacket(PacketEvent.Send<? extends PlayerMoveC2SPacket> event) {
        if (!RotationUtil.getRotationPlayer().onGround)
            return;
        if (blink.getValue() && cancelling) {
            event.setCancelled(true);
        }
    }

    protected Entity findTarget() {
        // TODO: make this better!
        Entity closest = null;
        Entity bestEnemy = null;

        double bestAngle = 360.0;
        float lowest = Float.MAX_VALUE;

        double distance = Double.MAX_VALUE;
        double closestEnemy = Double.MAX_VALUE;

        for (Entity entity : mc.world.getEntities()) {
            if (!isValid(entity) || RotationUtil.getRotationPlayer().squaredDistanceTo(entity)
                    > MathUtil.square(targetRange.getValue())) {
                continue;
            }

            if (!mc.player.canSee(entity) && visCheck.getValue()) {
                continue;
            }

            double dist = mc.player.squaredDistanceTo(entity);
            if (targetMode.getValue() == AuraTarget.Angle) {
                double angle = RotationUtil.getAngle(entity, 1.75);
                if (angle < bestAngle) {
                    closest = entity;
                    bestAngle = angle;
                }

                continue;
            }

            if (prioEnemies.getValue()
                    && entity instanceof PlayerEntity
                    && Managers.ENEMIES.contains((PlayerEntity) entity)
                    && dist < closestEnemy) {
                bestEnemy = entity;
                closestEnemy = dist;
            }

            if (isInRange(RotationUtil.getRotationPlayer(), entity)) {
                if (entity instanceof LivingEntity) {
                    float h = EntityUtil.getHealth((LivingEntity) entity);
                    if (h < lowest) {
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

            if (closest == null) {
                closest = entity;
                distance = dist;
                continue;
            }

            if (dist < distance) {
                closest = entity;
                distance = dist;
            }
        }

        return bestEnemy != null ? bestEnemy : closest;
    }

    public boolean isInRange(Entity from, Entity target) {
        return isInRange(from.getPos(), target);
    }

    public boolean isInRange(Vec3d from, Entity target) {
        double distance = from.squaredDistanceTo(target.getPos());
        if (distance >= MathUtil.square(range.getValue())) {
            return false;
        }

        if (distance < MathUtil.square(wallRange.getValue())) {
            return true;
        }
        // probably doesn't work as this check is retarded
        return mc.world.raycastBlock(
                 new Vec3d(from.x,
                         from.y + mc.player.getEyeHeight(mc.player.getPose()),
                         from.z),
                 new Vec3d(target.getX(),
                         target.getY() + target.getEyeHeight(mc.player.getPose()),
                         target.getZ()),
                 new BlockPos((int) from.x, (int) from.y, (int) from.z),
                VoxelShapes.cuboid(target.getBoundingBox()),
                 mc.world.getBlockState(new BlockPos((int) from.x, (int) from.y, (int) from.z))) == null;

    }

    @Override
    public boolean isValid(Entity entity) {
        if (entity == null
                || EntityUtil.isDead(entity)
                || entity.equals(mc.player)
                || entity.equals(mc.player.getVehicle())
                || entity instanceof PlayerEntity
                && Managers.FRIENDS.contains((PlayerEntity) entity)
                || entity instanceof ExperienceBottleEntity
                || entity instanceof ItemEntity
                || entity instanceof ArrowEntity
                || entity instanceof EndCrystalEntity) {
            return false;
        }

        return super.isValid(entity);
    }

    public boolean hasEntity(String id) {
        return entityDataArrayList.stream().anyMatch(entityData -> Objects.equals(entityData.id, id));
    }

    public static class EntityData {
        private final String id;
        private final long time;

        public EntityData(String id, long time) {
            this.id = id;
            this.time = time;
        }

        public String getId() {
            return id;
        }

        public long getTime() {
            return time;
        }
    }
}
