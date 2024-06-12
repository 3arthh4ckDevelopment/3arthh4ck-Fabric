package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.ItemUtil;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPacket extends CPacketPlayerListener implements Globals
{
    private final NoRotate module;

    public ListenerCPacket(NoRotate module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        onPacket(event.getPacket());
    }

    private void onPacket(PlayerMoveC2SPacket packet)
    {
        if (module.noSpoof.getValue()
                && !Managers.ROTATION.isBlocking()
                && (ItemUtil.isThrowable(mc.player.getActiveItem().getItem())
                || mc.player.getActiveItem().getItem() instanceof BowItem)
                && packet.getYaw(mc.player.yaw) != mc.player.yaw)
        {
            ((ICPacketPlayer) packet).setYaw(mc.player.yaw);
            ((ICPacketPlayer) packet).setPitch(mc.player.pitch);
        }
    }

}