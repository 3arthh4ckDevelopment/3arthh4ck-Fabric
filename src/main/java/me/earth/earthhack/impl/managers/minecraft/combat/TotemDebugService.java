package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.mixins.network.server.IEntityStatusS2CPacket;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;

public class TotemDebugService extends SubscriberImpl implements Globals
{
    private volatile long time;

    public TotemDebugService()
    {
        this.listeners.add(new ReceiveListener<>(EntityStatusS2CPacket.class, e ->
        {
            PlayerEntity player = mc.player;
            IEntityStatusS2CPacket packet = (IEntityStatusS2CPacket) e.getPacket();
            if (player != null
                && packet.getLogicOpcode() == 35
                && packet.getEntityId() == player.getId())
            {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info(
                    "Pop, last pop: " + (t - time) + "ms");
                time = t;
            }
        }));
        // SPacketEntityMetadata or SPacketEntityProperties might be earlier.
        this.listeners.add(new ReceiveListener<>(HealthUpdateS2CPacket.class, e ->
        {
            if (e.getPacket().getHealth() <= 0.0f)
            {
                long t = System.currentTimeMillis();
                Earthhack.getLogger().info(
                    "Death, last pop: " + (t - time) + "ms");
                time = t;
            }
        }));
    }

    public static void trySubscribe(EventBus eventBus)
    {
        // TODO:

        // Argument<Boolean> a = DevArguments.getInstance().getArgument("totems");
        // if (a == null || a.getValue())
        // {
        //     Earthhack.getLogger().info("TotemDebugger loaded.");
        //     eventBus.subscribe(new TotemDebugService());
        // }
    }

}
