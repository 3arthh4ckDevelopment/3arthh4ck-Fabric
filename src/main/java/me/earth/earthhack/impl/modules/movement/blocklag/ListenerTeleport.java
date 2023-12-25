package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class ListenerTeleport extends ModuleListener<BlockLag, PacketEvent.Post<TeleportConfirmC2SPacket>> {
    public ListenerTeleport(BlockLag module) {
        super(module, PacketEvent.Post.class, TeleportConfirmC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<TeleportConfirmC2SPacket> event) {
        PlayerEntity player = mc.player;
        if (player != null
                && module.onTeleport.getValue()
                && !module.blockTeleporting
                && module.ateChorus) {
            if(!ListenerTick.burrow.isEnabled()){
                ListenerTick.burrow.enable();
                module.ateChorus=false;
                if(module.chorusDisable.getValue()
                        || !BlockUtil.isReplaceable(
                        module.pos.add(0,1,0)))
                        // module.pos.add(0,0.2,0))) TODO: use a vec3d?
                    ListenerTick.burrow.disable();
            }
        }
    }
}
