package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.IInteractAtHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInteractEntityC2SPacket.InteractAtHandler.class)
public abstract class MixinInteractAtHandler implements IInteractAtHandler
{
    @Override
    @Accessor(value = "pos")
    public abstract void setVec(Vec3d vec3d);

    @Override
    @Accessor(value = "hand")
    public abstract void setHand(Hand hand);
}
