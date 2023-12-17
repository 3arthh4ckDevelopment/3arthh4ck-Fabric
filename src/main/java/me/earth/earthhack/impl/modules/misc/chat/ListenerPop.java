package me.earth.earthhack.impl.modules.misc.chat;

import me.earth.earthhack.impl.event.events.misc.TotemPopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

final class ListenerPop extends ModuleListener<Chat, TotemPopEvent> {
    public ListenerPop(Chat module) {
        super(module, TotemPopEvent.class);
    }

    @Override
    public void invoke(TotemPopEvent event) {
        ClientPlayerEntity player = mc.player;
        //noinspection ConstantConditions
        if (module.popMessage.getValue()
            && module.popLagTimer.passed(module.popLagDelay.getValue())
            && player != null
            && !player.equals(event.getEntity())
            && event.getEntity() instanceof PlayerEntity
            && event.getEntity().getName() != null
            && !Managers.FRIENDS.contains(event.getEntity())
            && module.sent.add(event.getEntity().getName().getString()))
        {
            String name = event.getEntity().getName().getString();
            player.networkHandler.sendChatMessage("/msg " + name + " " + Chat.LAG_MESSAGE);
            module.popLagTimer.reset();
        }
    }

}
