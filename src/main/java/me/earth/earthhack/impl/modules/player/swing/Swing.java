package me.earth.earthhack.impl.modules.player.swing;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

public class Swing extends Module {
    public final NumberSetting<Integer> swingSpeed =
            register(new NumberSetting<>("Swing-Delay", 6, 0, 20));
    public final BooleanSetting clientside =
            register(new BooleanSetting("ClientSide", false));
    protected final EnumSetting<SwingModes> mode =
            register(new EnumSetting<>("Hand", SwingModes.Mainhand));


    public Swing() {
        super("Swing", Category.Player);
        this.setData(new SwingData(this));
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null && mc.world == null) return;

            switch (mode.getValue()){
                case Mainhand:
                    mc.player.preferredHand = Hand.MAIN_HAND;
                break;

                case Offhand:
                    mc.player.preferredHand = Hand.OFF_HAND;
                break;

            } // Using a switch rn for the future, since I plan on implementing Shuffle/switch for this
        }));
        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class,e -> {
            if (!clientside.getValue()) {
                if (e.getPacket() instanceof HandSwingC2SPacket) {
                    switch (mode.getValue())
                    {
                        case Mainhand:
                           if (((HandSwingC2SPacket) e.getPacket()).getHand() != Hand.MAIN_HAND)
                           {
                               e.setCancelled(true);
                               mc.player.swingHand(Hand.MAIN_HAND);
                           }
                        break; // Using a switch rn for the future, since I plan on implementing Shuffle/Switch for this

                        case Offhand:
                           if (((HandSwingC2SPacket) e.getPacket()).getHand() != Hand.OFF_HAND)
                           {
                                e.setCancelled(true);
                                mc.player.swingHand(Hand.OFF_HAND);
                           }
                        break;
                    }
                }
            }
        }));
    }

}
