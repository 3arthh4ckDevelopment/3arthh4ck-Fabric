package me.earth.earthhack.impl.core.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Made by cattyn <a href="https://github.com/mioclient/oyvey-ported/blob/master/src/main/java/me/alpha432/oyvey/mixin/MixinWorldRenderer.java">...</a>
 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Unique
    private static final ModuleCache<BlockHighlight>
            BLOCK_HIGHLIGHT = Caches.getModule(BlockHighlight.class);

    @Inject(method = "render", at = @At("RETURN"))
    private void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local MatrixStack matrices)
    {
        MinecraftClient.getInstance().getProfiler().push("earthhack-render-3d");
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        Bus.EVENT_BUS.post(new Render3DEvent(matrices, tickCounter.getTickDelta(true)));
        MinecraftClient.getInstance().getProfiler().pop();
    }

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void drawBlockOutlineHook(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (BLOCK_HIGHLIGHT.isEnabled()) {
            ci.cancel();
        }
    }
}