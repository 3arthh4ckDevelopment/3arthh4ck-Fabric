package me.earth.earthhack.impl.modules.player.blink;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.modules.player.blink.mode.PacketMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;

import java.util.LinkedList;
import java.util.Queue;

//TODO: mode pulse
//TODO: auto disable stuff
public class Blink extends DisablingModule
{
    protected final Setting<PacketMode> packetMode =
            register(new EnumSetting<>("Packets", PacketMode.C2SPacket));
    protected final Setting<Boolean> lagDisable    =
            register(new BooleanSetting("LagDisable", false));

    protected final Queue<Packet<?>> packets = new LinkedList<>();
    protected PlayerEntity fakePlayer;
    protected boolean shouldSend;

    public Blink()
    {
        super("Blink", Category.Player);
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerPacket(this));
        SimpleData data = new SimpleData(this,
            "Suppresses all movement packets you send to the server. It will look" +
            " like you don't move at all and then teleport when" +
            " you disable this module.");
        data.register(packetMode,
                """
                        - All : Cancels all packets. Will cause packet spam.
                        - C2SPacket : Only cancels movement packets.
                        - Filtered : Leaves some packets through, still spammy.""");
        data.register(lagDisable,
                "Disable this module when the server lags you back.");
        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        if (mc.player == null)
        {
            this.disable();
            return;
        }

        // TODO: fakeplayer

        // fakePlayer = PlayerUtil
        //         .createFakePlayerAndAddToWorld(mc.player.getGameProfile());
    }

    @Override
    protected void onDisable()
    {
        PlayerUtil.removeFakePlayer(fakePlayer);

        if (shouldSend && mc.getNetworkHandler() != null)
        {
            CollectionUtil.emptyQueue(packets, p -> mc.getNetworkHandler()
                                                      .sendPacket(p));
        }
        else
        {
            packets.clear();
        }

        shouldSend = true;
    }

    @Override
    public void onShutDown()
    {
        shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDeath()
    {
        shouldSend = false;
        super.onShutDown();
    }

    @Override
    public void onDisconnect()
    {
        shouldSend = false;
        super.onShutDown();
    }

}
