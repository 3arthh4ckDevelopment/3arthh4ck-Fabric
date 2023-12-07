package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;

public class AutoLog extends Module
{
    protected final Setting<Float> health       =
            register(new NumberSetting<>("Health", 5.0f, 0.1f, 19.5f));
    protected final Setting<Integer> totems     =
            register(new NumberSetting<>("Totems", 0, 0, 10));
    protected final Setting<Float> enemy        =
            register(new NumberSetting<>("Enemy", 12.0f, 0.0f, 100.0f));
    protected final Setting<Boolean> absorption =
            register(new BooleanSetting("Absorption", false));

    protected ServerInfo serverData;
    protected String message;
    protected boolean awaitScreen;

    public AutoLog()
    {
        super("AutoLog", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerScreen(this));
        this.setData(new AutoLogData(this));
    }

    @Override
    protected void onDisable()
    {
        awaitScreen = false;
    }

    public void disconnect(float health, PlayerEntity closest, int totems)
    {
        this.message = "AutoLogged with "
            + MathUtil.round(health, 1) + " health and " + totems + " Totem"
            + (totems == 1 ? "" : "s") + " remaining."
            + (closest == null
                ? ""
                : " Closest Enemy: " + closest.getName() + ".");

        this.serverData = mc.getCurrentServerEntry();
        this.awaitScreen = true;
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection == null)
        {
            mc.world.sendPacket(new DisconnectS2CPacket(Text.of(message)));
        }
        else
        {
            ServerUtil.disconnectFromMC(message);
        }
    }

}
