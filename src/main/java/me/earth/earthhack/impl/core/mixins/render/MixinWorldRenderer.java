package me.earth.earthhack.impl.core.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Made by cattyn <a href="https://github.com/mioclient/oyvey-ported/blob/master/src/main/java/me/alpha432/oyvey/mixin/MixinWorldRenderer.java">...</a>
 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Inject(method = "render", at = @At("RETURN"))
    private void render(MatrixStack matrices,
                        float tickDelta,
                        long limitTime,
                        boolean renderBlockOutline,
                        Camera camera,
                        GameRenderer gameRenderer,
                        LightmapTextureManager lightmapTextureManager,
                        Matrix4f positionMatrix, CallbackInfo ci)
    {
        MinecraftClient.getInstance().getProfiler().push("earthhack-render-3d");
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        Bus.EVENT_BUS.post(new Render3DEvent(matrices, tickDelta));
        MinecraftClient.getInstance().getProfiler().pop();
    }
}