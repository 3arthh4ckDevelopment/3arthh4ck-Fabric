package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.network.IEntitySpawnS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiFriendPop;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.BreakValidity;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Listens for EntitySpawnS2CPackets.
 * Handles SimulatePlace in AutoCrystal.
 *
 * Also handles the Instant setting in AutoCrystal, since the EntitySpawnS2CPacket is
 * received by the client once the entity we've placed (the Crystal) actually exists
 * on the server and can be attacked.
 */
final class ListenerSpawnObject extends
        ModuleListener<AutoCrystal, PacketEvent.Receive<EntitySpawnS2CPacket>>
{
    private static final ModuleCache<AntiSurround> ANTISURROUND =
            Caches.getModule(AntiSurround.class);
    private static final SettingCache<Float, NumberSetting<Float>, Safety> DMG =
            Caches.getSetting(Safety.class, Setting.class, "MaxDamage", 4.0f);

    public ListenerSpawnObject(AutoCrystal module)
    {
        super(module,
                PacketEvent.Receive.class,
                Integer.MIN_VALUE,
                EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        try
        {
            onEvent(event);
        }
        catch (Throwable t) // ConcurrentModification in our ArmorList
        {
            t.printStackTrace();
        }
    }

    private void onEvent(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        World world = mc.world;
        if (mc.player == null
            || world == null
            || module.basePlaceOnly.getValue()
            || event.getPacket().getEntityData() != 51
            || mc.world == null
            || !module.spectator.getValue() && mc.player.isSpectator()
            || module.stopWhenEating.getValue() && module.isEating()
            || module.stopWhenEatingOffhand.getValue() && module.isEatingOffhand()
            || module.stopWhenMining.getValue() && module.isMining()
            || ((IEntitySpawnS2CPacket) event.getPacket()).isAttacked())
        {
            return;
        }

        EntitySpawnS2CPacket packet = event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EndCrystalEntity entity = new EndCrystalEntity(world, x, y, z);

        if (module.simulatePlace.getValue() != 0)
        {
            event.addPostEvent(() ->
            {
                if (mc.world == null)
                {
                    return;
                }

                Entity e = mc.world.getEntityById(packet.getEntityId());
                if (e instanceof EndCrystalEntity)
                {
                    module.crystalRender.onSpawn((EndCrystalEntity) e);
                }
            });
        }

        if (!module.instant.getValue()
            || module.isPingBypass()
            || !module.breakTimer.passed(module.breakDelay.getValue())
            || ANTISURROUND.returnIfPresent(AntiSurround::isActive, false))
        {
            return;
        }

        BlockPos pos = BlockPos.ofFloored(x, y, z);
        CrystalTimeStamp stamp = module.placed.get(pos);
        entity.setShowBottom(false);
        entity.setId(packet.getEntityId());
        entity.setUuid(packet.getUuid());

        boolean attacked = false;
        if ((!module.alwaysCalc.getValue()
                || pos.equals(module.bombPos)
                    && module.alwaysBomb.getValue())
            && stamp != null
            && stamp.isValid()
            && (stamp.getDamage() > module.slowBreakDamage.getValue()
                || stamp.isShield()
                || module.breakTimer.passed(module.slowBreakDelay.getValue())
                || pos.down().equals(module.antiTotemHelper.getTargetPos())))
        {
            if (pos.equals(module.bombPos))
            {
                // should probably set the block underneath
                // to air when calcing self damage...
                module.bombPos = null;
            }

            float damage = checkPos(entity);
            if (damage <= -1000.0f)
            {
                MutableWrapper<Boolean> a = new MutableWrapper<>(false);
                module.rotation = module.rotationHelper.forBreaking(entity, a);
                // set it once more once we got the real entity
                event.addPostEvent(() ->
                {
                    if (mc.world != null)
                    {
                        Entity e = mc.world.getEntityById(packet.getEntityId());
                        if (e != null)
                        {
                            module.post.add(
                                    module.rotationHelper.post(e, a));
                            module.rotation =
                                    module.rotationHelper.forBreaking(e, a);

                            module.setCrystal(e);
                        }
                    }
                });

                return;
            }

            if (damage < 0.0f)
            {
                return;
            }

            if (damage > module.shieldSelfDamage.getValue() && stamp.isShield())
            {
                return;
            }

            attack(packet,
                    event,
                    entity,
                    stamp.getDamage() <= module.slowBreakDamage.getValue());
            attacked = true;
        }
        else if (module.asyncCalc.getValue() || module.alwaysCalc.getValue())
        {
            List<PlayerEntity> players = Managers.ENTITIES.getPlayers();
            if (players == null)
            {
                return;
            }

            float self = checkPos(entity);
            if (self < 0.0f)
            {
                // TODO: ROTATIONS HERE?
                return;
            }

            boolean slow = true;
            boolean attack = false;
            for (PlayerEntity player : players)
            {
                if (player == null
                    || EntityUtil.isDead(player)
                    || player.squaredDistanceTo(x, y, z) > 144)
                {
                    continue;
                }

                if (Managers.FRIENDS.contains(player)
                    && (!module.isSuicideModule()
                    || !player.equals(mc.player)
                        && !player.equals(RotationUtil.getRotationPlayer())))
                {
                    if (module.antiFriendPop.getValue()
                                            .shouldCalc(AntiFriendPop.Break))
                    {
                        if (module.damageHelper.getDamage(entity, player)
                                > EntityUtil.getHealth(player) - 0.5f)
                        {
                            attack = false;
                            break;
                        }
                    }

                    continue;
                }

                float dmg = module.damageHelper.getDamage(entity, player);
                if ((dmg > self
                        || module.suicide.getValue()
                            && dmg >= module.minDamage.getValue())
                    && dmg > module.minBreakDamage.getValue()
                    && (dmg > module.slowBreakDamage.getValue()
                        || module.shouldDanger()
                        || module.breakTimer.passed(module.slowBreakDelay
                                                          .getValue())))
                {
                    slow = slow && dmg <= module.slowBreakDamage.getValue();
                    attack = true;
                }
            }

            if (attack)
            {
                attack(packet, event, entity,
                       (stamp == null || !stamp.isShield()) && slow);
                attacked = true;
            }
            else if (stamp != null
                && stamp.isShield()
                && self >= 0.0f
                && self <= module.shieldSelfDamage.getValue())
            {
                attack(packet, event, entity, false);
                attacked = true;
            }
        }

        if (module.spawnThread.getValue()
            && (!module.spawnThreadWhenAttacked.getValue() || attacked))
        {
            module.threadHelper.schedulePacket(event);
        }
    }

    private void attack(EntitySpawnS2CPacket packet,
                        PacketEvent.Receive<?> event,
                        EndCrystalEntity entityIn,
                        boolean slow)
    {
        HelperInstantAttack.attack(module, packet, event, entityIn, slow);
    }

    private float checkPos(Entity entity)
    {
        BreakValidity validity = HelperUtil.isValid(module, entity, true);
        switch (validity) {
            // TODO: wtf is this magic number shit
            case INVALID -> {
                return -1.0f;
            }
            case ROTATIONS -> {
                float damage = getSelfDamage(entity);
                if (damage < 0) {
                    return damage;
                }
                return -1000.0f - damage;
            }
            default -> {
            }
        }

        return getSelfDamage(entity);
    }

    private float getSelfDamage(Entity entity)
    {
        float damage = module.damageHelper.getDamage(entity);
        if (damage > EntityUtil.getHealth(mc.player) - 1.0f
                || damage > DMG.getValue())
        {
            Managers.SAFETY.setSafe(false);
        }

        return damage > module.maxSelfBreak.getValue()
                || damage > EntityUtil.getHealth(mc.player) - 1.0f
                && !module.suicide.getValue()
                    ? -1.0f
                    : damage;
    }

}
