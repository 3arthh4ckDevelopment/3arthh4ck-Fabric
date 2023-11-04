package me.earth.earthhack.impl.modules.client.accountspoof;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.impl.core.mixins.network.client.ILoginSuccessS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

import java.util.UUID;

final class ListenerLoginStart extends ModuleListener<AccountSpoof, PacketEvent.Receive<LoginSuccessS2CPacket>> {
    public ListenerLoginStart(AccountSpoof module) {
        super(module, PacketEvent.Receive.class, LoginSuccessS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<LoginSuccessS2CPacket> event) {
        if (mc.isInSingleplayer() && !module.spoofSP.getValue()) {
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(module.uuid.getValue());
        } catch (IllegalArgumentException e) {
            ChatUtil.sendMessageScheduled(TextColor.RED + "Bad UUID for AccountSpoof: " + e.getMessage());
            uuid = UUID.randomUUID();
        }

        ((ILoginSuccessS2CPacket) event.getPacket()).setProfile(new GameProfile(uuid, module.accountName.getValue()));
    }

}
