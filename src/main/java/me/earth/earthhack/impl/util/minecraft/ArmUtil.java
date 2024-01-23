package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.ILivingEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

import java.util.Objects;

public class ArmUtil implements Globals
{
    public static void swingPacket(Hand hand)
    {
        Objects.requireNonNull(
                mc.getNetworkHandler()).sendPacket(new HandSwingC2SPacket(hand));
    }

    public static void swingArmNoPacket(Hand hand)
    {
        // PingBypass.sendPacket(new S2CSwingPacket(hand));
        if (!mc.player.handSwinging
                || mc.player.handSwingTicks >=
                ((ILivingEntity) mc.player).armSwingAnimationEnd() / 2
                || mc.player.handSwingTicks < 0)
        {
            mc.player.handSwingTicks = -1;
            mc.player.handSwinging = true;
            mc.player.preferredHand = hand;
        }
    }

    public static void swingArm(Hand hand)
    {
        // PingBypass.sendPacket(new S2CSwingPacket(hand));
        mc.player.swingHand(hand);
    }

}

