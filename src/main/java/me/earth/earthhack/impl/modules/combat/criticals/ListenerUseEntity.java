package me.earth.earthhack.impl.modules.combat.criticals;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

/**
 *  Optional offsets:
 * <p>
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y + 0.05, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y + 0.03, pos.z, false));
 *  me.earth.earthhack.pingbypass.PingBypass.sendToActualServer();(new CPacketPlayer.Position(
 *      pos.x, pos.y, pos.z, false))
 */
final class ListenerUseEntity extends
        ModuleListener<Criticals, PacketEvent.Send<PlayerInteractEntityC2SPacket>>
{
    private static final ModuleCache<KillAura> KILL_AURA =
            Caches.getModule(KillAura.class);

    public ListenerUseEntity(Criticals module)
    {
        super(module, PacketEvent.Send.class, PlayerInteractEntityC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractEntityC2SPacket> event)
    {

        if (/*event.getPacket(). == PlayerInteractEntityC2SPacket.InteractType.ATTACK
                && */mc.player.onGround
                && !mc.options.jumpKey.isPressed()
                && !(mc.player.isSubmergedInWater() || mc.player.isInLava())
                && module.timer.passed(module.delay.getValue())
                && !(module.movePause.getValue() && MovementUtil.isMoving()))
        {
            PlayerInteractEntityC2SPacket packet = event.getPacket();
            Entity entity = ((IPlayerInteractEntityC2S) packet).getAttackedEntity();
            if (!module.noDesync.getValue()
                    || entity instanceof LivingEntity)
            {
                Vec3d vec = RotationUtil.getRotationPlayer()
                                        .getPos();
                Vec3d pos = KILL_AURA.returnIfPresent(
                                k -> k.criticalCallback(vec),
                                vec);

                switch (module.mode.getValue()) {
                    case Packet -> {
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y + 0.0625101,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y + 1.1E-5,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y,
                                        pos.z,
                                        false));
                    }
                    case Bypass -> {
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y + 0.062600301692775,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y + 0.07260029960661,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y,
                                        pos.z,
                                        false));
                        NetworkUtil.send(
                                new PlayerMoveC2SPacket.PositionAndOnGround(
                                        pos.x,
                                        pos.y,
                                        pos.z,
                                        false));
                    }
                    case Jump -> mc.player.jump();
                    case MiniJump -> {
                        mc.player.jump();
                        mc.player.setVelocity(
                                mc.player.getVelocity().getX(),
                                mc.player.getVelocity().getY() / 2.0,
                                mc.player.getVelocity().getZ());
                    }
                    default -> {
                    }
                }

                module.timer.reset();
            }
        }
    }

}
