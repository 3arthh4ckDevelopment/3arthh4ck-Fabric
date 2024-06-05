package me.earth.earthhack.impl.modules.client.accountspoof;

import me.earth.earthhack.impl.core.mixins.network.client.ILoginHelloC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;

import java.util.UUID;

final class ListenerLoginStart extends ModuleListener<AccountSpoof, PacketEvent.Send<LoginHelloC2SPacket>> {
    public ListenerLoginStart(AccountSpoof module) {
        super(module, PacketEvent.Send.class, LoginHelloC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<LoginHelloC2SPacket> event) {
        if (mc.isInSingleplayer() && !module.spoofSP.getValue())
            return;

        ILoginHelloC2SPacket loginAccessor = ILoginHelloC2SPacket.class.cast(event.getPacket());

        UUID uuid;
        try {
            uuid = UUID.fromString(module.uuid.getValue());
        } catch (IllegalArgumentException e) {
            ChatUtil.sendMessageScheduled(TextColor.RED + "Bad UUID for AccountSpoof: " + e.getMessage(), module.getName());
            uuid = UUID.randomUUID();
        }
        loginAccessor.earthhack$setName(module.accountName.getValue());
        loginAccessor.earthhack$setUuid(uuid);
    }

}
