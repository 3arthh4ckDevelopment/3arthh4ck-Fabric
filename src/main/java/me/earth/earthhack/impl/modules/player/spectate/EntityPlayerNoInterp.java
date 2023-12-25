package me.earth.earthhack.impl.modules.player.spectate;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class EntityPlayerNoInterp extends OtherClientPlayerEntity
        implements IEntityNoInterp, Globals
{
    public EntityPlayerNoInterp(ClientWorld worldIn)
    {
        this(worldIn, mc.player.getGameProfile());
    }

    public EntityPlayerNoInterp(ClientWorld worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    public double getNoInterpX() { return getX(); }

    @Override
    public double getNoInterpY() { return getY(); }

    @Override
    public double getNoInterpZ() { return getZ(); }

    @Override
    public void setNoInterpX(double x) { }

    @Override
    public void setNoInterpY(double y) { }

    @Override
    public void setNoInterpZ(double z) { }

    @Override
    public int getPosIncrements() { return 0; }

    @Override
    public void setPosIncrements(int posIncrements) { }

    @Override
    public float getNoInterpSwingAmount() { return 0; }

    @Override
    public float getNoInterpSwing() { return 0; }

    @Override
    public float getNoInterpPrevSwing() { return 0; }

    @Override
    public void setNoInterpSwingAmount(float noInterpSwingAmount) { }

    @Override
    public void setNoInterpSwing(float noInterpSwing) { }

    @Override
    public void setNoInterpPrevSwing(float noInterpPrevSwing) { }

    @Override
    public boolean isNoInterping() { return false; }

    @Override
    public void setNoInterping(boolean noInterping) { }
}
