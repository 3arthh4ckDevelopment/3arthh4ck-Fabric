package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.IPlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
@Mixin(PlayerActionC2SPacket.class)
public abstract class MixinPlayerActionC2S implements IPlayerActionC2SPacket
{
    @Unique
    private boolean clientSideBreaking;
    @Unique
    private boolean normalDigging;

    @Unique
    @Override
    public void earthhack$setClientSideBreaking(boolean breaking)
    {
        clientSideBreaking = breaking;
    }

    @Unique
    @Override
    public boolean earthhack$isClientSideBreaking()
    {
        return clientSideBreaking;
    }

    @Unique
    @Override
    public void earthhack$setNormalDigging(boolean normalDigging)
    {
        this.normalDigging = normalDigging;
    }

    @Unique
    @Override
    public boolean earthhack$isNormalDigging()
    {
        return normalDigging;
    }
}
