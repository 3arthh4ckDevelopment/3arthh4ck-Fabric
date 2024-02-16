package me.earth.earthhack.impl.modules.player.blink.mode;

import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public enum PacketMode
{
    All()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return true;
        }
    },
    C2SPacket()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return packet instanceof ServerPlayPacketListener;
        }
    },
    Filtered()
    {
        @Override
        public boolean shouldCancel(Packet<?> packet)
        {
            return !(packet instanceof ChatMessageC2SPacket
                        || packet instanceof TeleportConfirmC2SPacket
                        || packet instanceof CommonPongC2SPacket
                        // || packet instanceof CPacketTabComplete // TODO find this
                        || packet instanceof ClientStatusC2SPacket);
        }
    };

    public abstract boolean shouldCancel(Packet<?> packet);
}
