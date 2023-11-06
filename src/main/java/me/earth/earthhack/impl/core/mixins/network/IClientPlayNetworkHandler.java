package me.earth.earthhack.impl.core.mixins.network;

import com.mojang.authlib.GameProfile;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayNetworkHandler}
 */
public interface IClientPlayNetworkHandler
{
    boolean isDoneLoadingTerrain();

    void setDoneLoadingTerrain(boolean loaded);

    void setGameProfile(GameProfile gameProfile);

}
