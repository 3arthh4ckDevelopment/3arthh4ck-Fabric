package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

final class ListenerStopUsingItem extends ModuleListener<BowKiller, PacketEvent.Send<PlayerActionC2SPacket>> {

    public ListenerStopUsingItem(BowKiller module) {
        super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerActionC2SPacket> event) {
        if (!RotationUtil.getRotationPlayer().verticalCollision)
            return;
        if (event.getPacket().getAction() == PlayerActionC2SPacket.Action.RELEASE_USE_ITEM
                && RotationUtil.getRotationPlayer().getActiveItem().getItem() == Items.BOW && module.blockUnder) {
            module.cancelling = false;
            if (module.packetsSent >= module.runs.getValue() * 2
                    || module.always.getValue()) {
                PacketUtil.sendAction(ClientCommandC2SPacket.Mode.START_SPRINTING);
                if (module.cancelRotate.getValue()
                        && (RotationUtil.getRotationPlayer().yaw
                        != Managers.ROTATION.getServerYaw()
                        || RotationUtil.getRotationPlayer().pitch
                        != Managers.ROTATION.getServerPitch())) {
                    PacketUtil.doRotation(RotationUtil.getRotationPlayer().yaw,
                            RotationUtil.getRotationPlayer().pitch,
                            true);
                }

                for (int i = 0; i < module.runs.getValue() + module.buffer.getValue(); i++) {
                    if (i != 0 && i % module.interval.getValue() == 0) {
                        int id = Managers.POSITION.getTeleportID();
                        for (int j = 0; j < module.teleports.getValue(); j++) {
                            mc.player.networkHandler.sendPacket(new TeleportConfirmC2SPacket(++id));
                        }
                    }

                    double[] dir = MovementUtil.strafe(0.001);

                    if (module.rotate.getValue()) {
                        module.target = module.findTarget();
                        if (module.target != null) {
                            float[] rotations
                                    = module.rotationSmoother
                                    .getRotations(RotationUtil.getRotationPlayer(),
                                            module.target,
                                            module.height.getValue(),
                                            module.soft.getValue());
                            if (rotations != null) {
                                PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] : 0), mc.player.getY() + 0.00000000000013, mc.player.getZ() + (module.move.getValue() ? dir[1] : 0), rotations[0], rotations[1], true); // onground true
                                PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.getY() + 0.00000000000027, mc.player.getZ() + (module.move.getValue() ? dir[1] * 2 : 0), rotations[0], rotations[1], false); // onground false, jump
                            }
                        } else {
                            PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] : 0), mc.player.getY() + 0.00000000000013, mc.player.getZ() + (module.move.getValue() ? dir[1] : 0), mc.player.yaw, mc.player.pitch,  true); // onground true
                            PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.getY() + 0.00000000000027, mc.player.getZ() + (module.move.getValue() ? dir[1] * 2 : 0), mc.player.yaw, mc.player.pitch, false); // onground false, jump
                        }
                    } else {
                        PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] : 0), mc.player.getY() + 0.00000000000013, mc.player.getZ() + (module.move.getValue() ? dir[1] : 0), mc.player.yaw, mc.player.pitch, true); // onground true
                        PacketUtil.doPosRotNoEvent(RotationUtil.getRotationPlayer().getX() + (module.move.getValue() ? dir[0] * 2 : 0), mc.player.getY() + 0.00000000000027, mc.player.getZ() + (module.move.getValue() ? dir[1] * 2 : 0), mc.player.yaw, mc.player.pitch, false); // onground false, jump
                    }
                }
            }

            module.packetsSent = 0;
        }
    }

}