package me.earth.earthhack.impl.core.ducks;

import net.minecraft.client.network.PendingUpdateManager;

/**
 * Duck interface for {@link me.earth.earthhack.impl.core.mixins.MixinClientWorld}.
 */
public interface IClientWorld
{
    PendingUpdateManager earthhack$getPendingUpdateManager();
}
