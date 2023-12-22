package me.earth.earthhack.impl.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Render2DUtil implements Globals {
    public static void drawBlurryRect(MatrixStack matrix, float x, float y, float x1, float y1, int intensity, float size) {
        drawRect(
                matrix,
                (int) x,
                (int) y,
                (int) x1,
                (int) y1, new Color(50, 50, 50, 50).getRGB());
        blurArea(
                (int) x,
                (int) y,
                (int) x1 - (int) x,
                (int) y1 - (int) y,
                intensity, size, size);
    }

    public static void drawRect(MatrixStack matrix, float startX, float startY, float endX, float endY, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), startX, endY, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), endX, endY, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), endX, startY, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), startX, startY, 0.0F).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawBorderedRect(MatrixStack matrix, float x, float y, float x2, float y2, float lineSize, int color, int borderColor) {
        drawRect(matrix, x, y, x2, y2, color);
        drawRect(matrix, x, y, x + lineSize, y2, borderColor);
        drawRect(matrix, x2 - lineSize, y, x2, y2, borderColor);
        drawRect(matrix, x, y2 - lineSize, x2, y2, borderColor);
        drawRect(matrix, x, y, x2, y + lineSize, borderColor);
    }

    public static void drawCheckMark(float x, float y, int width, int color) {
        //TODO: rewrite
        /*
        float f = (color >> 24 & 255) / 255.0f;
        float f1 = (color >> 16 & 255) / 255.0f;
        float f2 = (color >> 8 & 255) / 255.0f;
        float f3 = (color & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(3f);
        GL11.glBegin(3);
        GL11.glColor4f(0, 0, 0, 1.f);
        GL11.glVertex2d(x + width - 6.25, y + 2.75f);
        GL11.glVertex2d(x + width - 11.5, y + 10.25f);
        GL11.glVertex2d(x + width - 13.75f, y + 7.75f);
        GL11.glEnd();
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x + width - 6.5, y + 3);
        GL11.glVertex2d(x + width - 11.5, y + 10);
        GL11.glVertex2d(x + width - 13.5, y + 8);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         */
    }

    public static void drawCheckeredBackground(float x, float y, float x2, float y2) {
    }

    public static void blurArea(int x, int y, int width, int height, float intensity, float blurWidth, float blurHeight) {
        //TODO: implement
    }

    public static void drawGradientRect(MatrixStack matrix, float left, float top, float right, float bottom, boolean sideways, int startColor, int endColor) {
        /*
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        if (sideways) {
            bufferbuilder.vertex(left, top, zLevel).color(f1, f2, f3, f).next();
            bufferbuilder.pos(left, bottom, zLevel).color(f1, f2, f3, f).next();
            bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).next();
            bufferbuilder.pos(right, top, zLevel).color(f5, f6, f7, f4).next();
        } else {
            bufferbuilder.pos(right, top, zLevel).color(f1, f2, f3, f).next();
            bufferbuilder.pos(left, top, zLevel).color(f1, f2, f3, f).next();
            bufferbuilder.pos(left, bottom, zLevel).color(f5, f6, f7, f4).next();
            bufferbuilder.pos(right, bottom, zLevel).color(f5, f6, f7, f4).next();
        }
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
         */
    }

}
