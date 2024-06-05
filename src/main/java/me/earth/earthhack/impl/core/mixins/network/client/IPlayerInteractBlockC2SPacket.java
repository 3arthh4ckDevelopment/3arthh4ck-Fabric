package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInteractBlockC2SPacket.class)
public interface IPlayerInteractBlockC2SPacket {
    @Accessor(value = "hand")
    void earthhack$setHand(Hand hand);
}
