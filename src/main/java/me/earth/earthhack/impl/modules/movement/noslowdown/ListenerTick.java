package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.Direction;

final class ListenerTick extends ModuleListener<NoSlowDown, TickEvent>
{
    public ListenerTick(NoSlowDown module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        Managers.NCP.setStrict(module.guiMove.getValue()
                                    && module.legit.getValue());
        if (event.isSafe()
                && module.legit.getValue()
                && module.items.getValue())
        {
            Item item = mc.player.getActiveItem().getItem();
            if (MovementUtil.isMoving()
                        && item.isFood()
                        || item instanceof BowItem
                        || item instanceof PotionItem)
            {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                        mc.player.getBlockPos(),
                        Direction.DOWN));
            }
            if (mc.player.getActiveHand() == null
                    && Managers.ACTION.isSprinting()
                    && module.sneakPacket.getValue())
            {
               mc.player.networkHandler.sendPacket(
                   new ClientCommandC2SPacket(
                           mc.player,
                           ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }

            if (((IEntity) mc.player).inWeb()
                    && !mc.player.isOnGround()
                    && module.useTimerWeb.getValue())
            {
                Managers.TIMER.setTimer(
                    module.timerSpeed.getValue().floatValue());
                module.usingTimer = true;
            } else if (module.usingTimer) {
                Managers.TIMER.reset();
                module.usingTimer = false;
            }
        }
    }

}
