package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.IPlayerActionC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerDigging extends
        ModuleListener<Speedmine, PacketEvent.Send<PlayerActionC2SPacket>>
{
    // private static final ModuleCache<Nuker> NUKER =
    //     Caches.getModule(Nuker.class);
    // private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
    //     Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
        Caches.getModule(AntiSurround.class);

    public ListenerDigging(Speedmine module)
    {
        super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerActionC2SPacket> event)
    {
        if (module.cancelNormalPackets.getValue()
            && ((IPlayerActionC2SPacket) event.getPacket()).earthhack$isNormalDigging()
            && (event.getPacket().getAction() ==
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
                || event.getPacket().getAction() ==
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
                || event.getPacket().getAction() ==
                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK))
        {
            event.setCancelled(true);
            return;
        }

        if (!PlayerUtil.isCreative(mc.player)
            && !ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            // && (!NUKER.isEnabled() || !NUKE.getValue())
            && (module.mode.getValue() == MineMode.Packet
                    || module.mode.getValue() == MineMode.Smart
                    || module.mode.getValue() == MineMode.Instant))
        {
            PlayerActionC2SPacket packet = event.getPacket();
            if (packet.getAction() ==
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
                    || packet.getAction() ==
                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK)
            {
                BlockPos pos = packet.getPos();
                if (!MineUtil.canBreak(pos))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

}
