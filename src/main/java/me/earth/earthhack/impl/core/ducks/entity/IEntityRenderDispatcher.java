package me.earth.earthhack.impl.core.ducks.entity;

/**
 * Duck interface for {@link net.minecraft.client.render.entity.EntityRenderDispatcher}.
 */
public interface IEntityRenderDispatcher
{
    void invokeSetupCameraTransform(float partialTicks, int pass);

    void invokeOrientCamera(float partialTicks);

    void invokeRenderHand(float partialTicks, int pass);

    void setLightmapUpdateNeeded(boolean needed);

}
