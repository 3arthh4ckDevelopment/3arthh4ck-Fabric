package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

final class ListenerTeleport
    extends ModuleListener<Surround, PacketEvent.Post<TeleportConfirmC2SPacket>>
{
    public ListenerTeleport(Surround module) {
        super(module, PacketEvent.Post.class, TeleportConfirmC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<TeleportConfirmC2SPacket> event) {
        PlayerEntity player = mc.player;

        if(module.autoOnTeleport.getValue()
                && player != null
                && module.teleport.getValue()
                && !module.isEnabled()){
            module.startPos = module.getPlayerPos();
            module.enable();
        }

        if (player != null
            && module.teleport.getValue()
            && !module.blockTeleporting) {
            module.startPos = module.getPlayerPos();
        }
    }

}
