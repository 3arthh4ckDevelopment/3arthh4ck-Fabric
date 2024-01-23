package me.earth.earthhack.impl.modules.player.spectate;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class PlayerEntityNoInterp extends OtherClientPlayerEntity
        implements IEntityNoInterp, Globals
{
    public PlayerEntityNoInterp(ClientWorld worldIn)
    {
        this(worldIn, mc.player.getGameProfile());
    }

    public PlayerEntityNoInterp(ClientWorld worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    public double earthhack$getNoInterpX() { return getX(); }

    @Override
    public double earthhack$getNoInterpY() { return getY(); }

    @Override
    public double earthhack$getNoInterpZ() { return getZ(); }

    @Override
    public void earthhack$setNoInterpX(double x) { }

    @Override
    public void earthhack$setNoInterpY(double y) { }

    @Override
    public void earthhack$setNoInterpZ(double z) { }

    @Override
    public int earthhack$getPosIncrements() { return 0; }

    @Override
    public void earthhack$setPosIncrements(int posIncrements) { }

    @Override
    public float earthhack$getNoInterpSwingAmount() { return 0; }

    @Override
    public float earthhack$getNoInterpSwing() { return 0; }

    @Override
    public float earthhack$getNoInterpPrevSwing() { return 0; }

    @Override
    public void earthhack$setNoInterpSwingAmount(float noInterpSwingAmount) { }

    @Override
    public void earthhack$setNoInterpSwing(float noInterpSwing) { }

    @Override
    public void earthhack$setNoInterpPrevSwing(float noInterpPrevSwing) { }

    @Override
    public boolean earthhack$isNoInterping() { return false; }

    @Override
    public void earthhack$setNoInterping(boolean noInterping) { }
}
