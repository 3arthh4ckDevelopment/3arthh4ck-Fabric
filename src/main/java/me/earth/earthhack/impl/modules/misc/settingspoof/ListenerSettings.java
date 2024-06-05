package me.earth.earthhack.impl.modules.misc.settingspoof;

import me.earth.earthhack.impl.core.mixins.network.client.IClientOptionsC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;

final class ListenerSettings extends
        ModuleListener<SettingSpoof, PacketEvent.Send<ClientOptionsC2SPacket>>
{
    public ListenerSettings(SettingSpoof module)
    {
        super(module, PacketEvent.Send.class, ClientOptionsC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<ClientOptionsC2SPacket> event)
    {
        IClientOptionsC2SPacket p = IClientOptionsC2SPacket.class.cast(event.getPacket());

        p.earthhack$setOptions(new SyncedClientOptions(
                module.getLanguage(p.earthhack$getOptions().language()),
                module.getRenderDistance(p.earthhack$getOptions().viewDistance()),
                module.getVisibility(p.earthhack$getOptions().chatVisibility()),
                module.getChatColors(p.earthhack$getOptions().chatColorsEnabled()),
                module.getModelParts(p.earthhack$getOptions().playerModelParts()),
                module.getHandSide(p.earthhack$getOptions().mainArm()),
                module.getChatFilter(p.earthhack$getOptions().filtersText()),
                module.getServerList(p.earthhack$getOptions().allowsServerListing())));
    }

}
