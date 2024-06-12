package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.FishingRodItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;


public class AutoFish extends Module
{
    protected final Setting<Boolean> openInv =
            register(new BooleanSetting("OpenInventory", true));
    protected final Setting<Float> delay     =
            register(new NumberSetting<>("Delay", 15.0f, 10.0f, 25.0f));
    protected final Setting<Double> range    =
            register(new NumberSetting<>("SoundRange", 2.0, 0.1, 5.0));

    protected boolean splash;
    protected int delayCounter;
    protected int splashTicks;
    protected int timeout;

    public AutoFish()
    {
        super("AutoFish", Category.Misc);
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
    }

    @Override
    protected void onEnable()
    {
        splash = false;
        splashTicks = 0;
        delayCounter = 0;
        timeout = 0;
    }

    protected void click()
    {
        if (!mc.player.getInventory().getMainHandStack().isEmpty()
                || mc.player.getInventory().getMainHandStack().getItem()
                instanceof FishingRodItem)
        {
            if (openInv.getValue()
                    || mc.currentScreen instanceof ChatScreen
                    || mc.currentScreen == null)
            {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                delayCounter = delay.getValue().intValue();
                timeout = 0;
            }
        }
    }

}