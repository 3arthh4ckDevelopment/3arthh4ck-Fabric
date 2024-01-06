package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

final class ListenerUpdate extends ModuleListener<FastEat, MotionUpdateEvent>
{
    public ListenerUpdate(FastEat module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE
                && module.mode.getValue() == FastEatMode.Update
                && module.isValid(mc.player.getActiveItem()))
        {
            Hand hand = mc.player.getActiveHand();
            //noinspection ConstantConditions
            if (hand == null) // this can happen!
            {
                hand = mc.player.getOffHandStack()
                                .equals(mc.player.getActiveItem())
                        ? Hand.OFF_HAND
                        : Hand.MAIN_HAND;
            }

            mc.player.networkHandler.sendPacket(
                    new PlayerInteractItemC2SPacket(hand, 1)); //TODO: i still don't know what the sequence is
        }
        else if (event.getStage() == Stage.POST
                && module.mode.getValue() == FastEatMode.Packet
                && module.isValid(mc.player.getActiveItem())
                && mc.player.getItemUseTimeLeft()
                    > module.speed.getValue() - 1
                && module.speed.getValue() < 25)
        {
            for (int i = 0; i < 32; i++)
            {
                /*
                PingBypass.sendToActualServer(
                        new CPacketPlayer(mc.player.isOnGround()));
                 */
            }

            /*
            PingBypass.sendToActualServer(new CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    Direction.DOWN));
             */

            mc.player.stopUsingItem();
        }
    }

}
