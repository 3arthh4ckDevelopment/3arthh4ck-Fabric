package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.core.mixins.network.server.IPlayerPositionLookS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.packetfly.util.Mode;
import me.earth.earthhack.impl.modules.movement.packetfly.util.TimeVec;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<PacketFly, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(PacketFly module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.mode.getValue() == Mode.Compatibility)
        {
            return;
        }

        IPlayerPositionLookS2CPacket packet =
                (IPlayerPositionLookS2CPacket) event.getPacket();

        if (mc.player.isAlive()
                && module.mode.getValue() != Mode.Setback
                && module.mode.getValue() != Mode.Slow
                && !(mc.currentScreen instanceof DownloadingTerrainScreen)
                && mc.world.isPosLoaded((int) mc.player.getX(), (int) mc.player.getZ())) // hmm
        {
            TimeVec vec = module.posLooks.remove(packet.getTeleportId());
            if (vec != null
                    && vec.x == packet.getX()
                    && vec.y == packet.getY()
                    && vec.z == packet.getZ())
            {
                event.setCancelled(true);
                return;
            }
        }

        module.teleportID.set(packet.getTeleportId());

        if (module.answer.getValue())
        {
            event.setCancelled(true);
            mc.execute(() ->
                    PacketUtil.handlePosLook(event.getPacket(),
                                             mc.player,
                                             true,
                                             false));
            return;
        }

        packet.setYaw(mc.player.getYaw());
        packet.setPitch(mc.player.getPitch());
    }

}

