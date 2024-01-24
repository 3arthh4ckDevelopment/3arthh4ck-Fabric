package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayerInteractionManager}
 */
public interface IClientPlayerInteractionManager {
    /**
     * Accessor for syncCurrentPlayItem.
     */
    void earthhack$syncItem();

    /**
     * Accessor for currentPlayerItem.
     *
     * @return currentPlayerItem.
     */
    int earthhack$getItem();

    /**
     * Accessor for blockHitDelay.
     *
     * @param delay set the delay.
     */
    void earthhack$setBlockHitDelay(int delay);

    /**
     * Accessor for blockHitDelay.
     *
     * @return blockHitDelay.
     */
    int earthhack$getBlockHitDelay();

    /**
     * Accessor for curBlockDamageMP.
     *
     * @return curBlockDamageMP.
     */
    float earthhack$getCurBlockDamageMP();

    /**
     * Accessor for curBlockDamageMP.
     *
     * @param damage set curBlockDamageMP.
     */
    void earthhack$setCurBlockDamageMP(float damage);

    /**
     * Accessor for isHittingBlock.
     *
     * @param hitting set isHittingBlock.
     */
    void earthhack$setIsHittingBlock(boolean hitting);

    /**
     * Accessor for isHittingBlock.
     *
     * @return isHittingBlock.
     */
    boolean earthhack$getIsHittingBlock();

    ClientPlayNetworkHandler getConnection();
}
