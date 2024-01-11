package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.impl.core.ducks.network.IPlayerActionC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

final class ListenerDigging extends
        ModuleListener<AntiSurround, PacketEvent.Send<PlayerActionC2SPacket>>
{
    private final AntiSurroundFunction function;

    public ListenerDigging(AntiSurround module)
    {
        super(module,
                PacketEvent.Send.class,
                -1000,
                PlayerActionC2SPacket.class);
        this.function = new PreCrystalFunction(module);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerActionC2SPacket> event)
    {
        if (event.isCancelled()
            || event.getPacket().getAction()
                    != PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
            || module.holdingCheck()
            || !module.preCrystal.getValue()
            || !((IPlayerActionC2SPacket) event.getPacket())
                                              .earthhack$isClientSideBreaking())
        {
            return;
        }

        module.onBlockBreak(
                event.getPacket().getPos(),
                Managers.ENTITIES.getPlayersAsync(),
                Managers.ENTITIES.getEntitiesAsync(),
                function);
    }

}
