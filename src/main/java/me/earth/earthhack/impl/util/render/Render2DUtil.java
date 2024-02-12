package me.earth.earthhack.impl.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class Render2DUtil implements Globals {

    public static double getScreenScale() {
        return mc.getWindow().getScaleFactor();
    }
    public static int getScreenWidth() {
        return mc.getWindow().getWidth();
    }
    public static int getScreenHeight() {
        return mc.getWindow().getHeight();
    }

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

    public static void roundedRect(MatrixStack matrix, float startX, float startY, float endX, float endY, float radius, int color) {
        drawRect(matrix, startX, startY, endX, endY, color);
    }

    public static void drawCheckMark(MatrixStack matrix, float x, float y, int width, int color) {
        drawLine(matrix, x + width - 6.5f, y + 3f, x + width - 11.5f, y + 10f, 1, color);
        drawLine(matrix, x + width - 11.5f, y + 10f, x + width - 13.5f, y + 8, 1, color);
    }

    public static void drawLine(MatrixStack matrix, float x, float y, float x1, float y1, float lineWidth, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        float directionX = x1 - x;
        float directionY = y1 - y;

        float lineLength = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        float normalizedX = directionX / lineLength;
        float normalizedY = -(directionY / lineLength);

        float width = lineWidth / 2.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x + normalizedY * width, y + normalizedX * width, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1 + normalizedY * width, y1 + normalizedX * width, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x1 - normalizedY * width, y1 - normalizedX * width, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix.peek().getPositionMatrix(), x - normalizedY * width, y - normalizedX * width, 0.0F).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawCheckeredBackground(float x, float y, float x2, float y2) {
    }

    public static void blurArea(int x, int y, int width, int height, float intensity, float blurWidth, float blurHeight) {
        //TODO: implement
    }

    public static void drawGradientRect(MatrixStack matrix, float left, float top, float right, float bottom, boolean sideways, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        Matrix4f posMatrix = matrix.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        if (sideways) {
            bufferBuilder.vertex(posMatrix, left, top, 0.0F).color(f1, f2, f3, f).next();
            bufferBuilder.vertex(posMatrix, left, bottom, 0.0F).color(f1, f2, f3, f).next();
            bufferBuilder.vertex(posMatrix, right, bottom, 0.0F).color(f5, f6, f7, f4).next();
            bufferBuilder.vertex(posMatrix, right, top, 0.0F).color(f5, f6, f7, f4).next();
        } else {
            bufferBuilder.vertex(posMatrix, right, top, 0.0F).color(f1, f2, f3, f).next();
            bufferBuilder.vertex(posMatrix, left, top, 0.0F).color(f1, f2, f3, f).next();
            bufferBuilder.vertex(posMatrix, left, bottom, 0.0F).color(f5, f6, f7, f4).next();
            bufferBuilder.vertex(posMatrix, right, bottom, 0.0F).color(f5, f6, f7, f4).next();
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

}
