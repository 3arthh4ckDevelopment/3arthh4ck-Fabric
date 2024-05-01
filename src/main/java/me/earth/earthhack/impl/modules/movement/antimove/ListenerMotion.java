package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerMotion extends ModuleListener<NoMove, MotionUpdateEvent>
{
    public ListenerMotion(NoMove module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE
                && module.mode.getValue() == StaticMode.Roof)
        {
            NetworkUtil.send(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX(), 10000, mc.player.getZ(), mc.player.onGround));
            module.disable();
        }
    }

}
