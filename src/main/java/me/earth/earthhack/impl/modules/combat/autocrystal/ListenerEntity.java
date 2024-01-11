package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.core.mixins.network.server.IEntityS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.EntityS2CPacketListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ListenerEntity extends EntityS2CPacketListener
{
    private final AutoCrystal module;

    public ListenerEntity(AutoCrystal module)
    {
        this.module = module;
        this.listeners.add(
            new ReceiveListener<>(EntityPositionS2CPacket.class, e ->
        {
            if (!shouldCalc())
            {
                return;
            }

            PlayerEntity p = getEntity(e.getPacket().getId());
            if (p != null)
            {
                double x = e.getPacket().getX();
                double y = e.getPacket().getY();
                double z = e.getPacket().getZ();
                onEvent(p, x, y, z);
            }
        }));
    }

    protected void onPacket(
            PacketEvent.Receive<EntityS2CPacket> event) { }

    @Override
    protected void onRotation(
            PacketEvent.Receive<EntityS2CPacket.Rotate> event) { }

    @Override
    protected void onPosition(
            PacketEvent.Receive<EntityS2CPacket.MoveRelative> event)
    {
        onEvent(event.getPacket());
    }

    @Override
    protected void onPositionRotation(
            PacketEvent.Receive<EntityS2CPacket.RotateAndMoveRelative> event)
    {
        onEvent(event.getPacket());
    }

    private void onEvent(EntityS2CPacket packet)
    {
        if (!shouldCalc())
        {
            return;
        }

        PlayerEntity p = getEntity(((IEntityS2CPacket) packet).getEntityId());
        if (p == null)
        {
            return;
        }

        double x = (p.getX() + packet.getDeltaX()) / 4096.0;
        double y = (p.getY() + packet.getDeltaY()) / 4096.0;
        double z = (p.getZ() + packet.getDeltaZ()) / 4096.0;

        onEvent(p, x, y, z);
    }

    private void onEvent(PlayerEntity player, double x, double y, double z)
    {
        Entity entity = RotationUtil.getRotationPlayer();
        if (entity != null
            && entity.squaredDistanceTo(x, y, z)
                < MathUtil.square(module.targetRange.getValue())
            && !Managers.FRIENDS.contains(player))
        {
            boolean enemied = Managers.ENEMIES.contains(player);
            // Scheduling is required since this event might get cancelled.
            Scheduler.getInstance().scheduleAsynchronously(() ->
            {
                if (mc.world == null)
                {
                    return;
                }

                List<PlayerEntity> enemies;
                if (enemied)
                {
                    enemies = new ArrayList<>(1);
                    enemies.add(player);
                }
                else
                {
                    enemies = Collections.emptyList();
                }

                PlayerEntity target = module.targetMode.getValue().getTarget(
                                                 Managers.ENTITIES.getPlayers(), // mc.world.getPlayers()
                                                 enemies,
                                                 module.targetRange.getValue());

                if (target == null || target.equals(player))
                {
                    module.threadHelper.startThread();
                }
            });
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean shouldCalc()
    {
        return module.multiThread.getValue()
                && module.entityThread.getValue()
                && (module.rotate.getValue() == ACRotate.None
                 || module.rotationThread.getValue() != RotationThread.Predict);
    }

    private PlayerEntity getEntity(int id)
    {
        List<Entity> entities = Managers.ENTITIES.getEntities();
        if (entities == null)
        {
            return null;
        }

        Entity entity = null;
        for (Entity e : entities)
        {
            if (e != null && e.getId() == id)
            {
                entity = e;
                break;
            }
        }

        if (entity instanceof PlayerEntity)
        {
            return (PlayerEntity) entity;
        }

        return null;
    }

}
