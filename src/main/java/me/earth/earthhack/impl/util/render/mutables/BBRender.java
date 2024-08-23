package me.earth.earthhack.impl.util.render.mutables;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class BBRender {
    public static void renderBox(MatrixStack matrix,
                                 BB bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth)
    {
        drawOutline(matrix, bb, lineWidth, outLineColor);
        drawBox(matrix, bb, color);
    }

    // TODO: is it possible to improve on the performance of this function?
    public static void renderBox(MatrixStack matrix,
                                 BB bb,
                                 Color color,
                                 float lineWidth)
    {
        drawOutline(matrix, bb, lineWidth, color);
        drawBox(matrix, bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.29f);
    }

    public static void drawOutline(MatrixStack matrix, BB bb, float lineWidth, Color color) {
        RenderUtil.startRender();
        RenderSystem.lineWidth(lineWidth);
        RenderUtil.color(color);
        fillOutline(matrix, bb);
        RenderSystem.lineWidth(1.0f);
        RenderUtil.endRender();
    }

    public static void drawBox(MatrixStack matrix, BB bb, Color color) {
        RenderUtil.startRender();
        RenderUtil.color(color);
        fillBox(matrix, bb);
        RenderUtil.endRender();
    }

    public static void drawBox(MatrixStack matrix, BB bb, float red, float green, float blue, float alpha) {
        RenderUtil.startRender();
        RenderUtil.color(red, green, blue, alpha);
        fillBox(matrix, bb);
        RenderUtil.endRender();
    }

    public static void drawBox(MatrixStack matrix, BB bb) {
        RenderUtil.startRender();
        fillBox(matrix, bb);
        RenderUtil.endRender();
    }

    public static void fillBox(MatrixStack matrix, BB boundingBox) {
        if (boundingBox != null) {
            Matrix4f posMatrix = matrix.peek().getPositionMatrix();

            RenderSystem.setShader(GameRenderer::getPositionProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMaxY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMinX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) boundingBox.getMaxX(),
                            (float) boundingBox.getMinY(),
                            (float) boundingBox.getMaxZ());

            bufferBuilder.end();
        }
    }

    public static void fillOutline(MatrixStack matrix, BB bb) {
        if (bb != null) {
            Matrix4f posMatrix = matrix.peek().getPositionMatrix();

            RenderSystem.setShader(GameRenderer::getPositionProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION);

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMinY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMinY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMinZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMinZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMaxX(), (float) bb.getMaxY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMaxZ());

            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMaxZ());
            bufferBuilder.vertex(posMatrix, (float) bb.getMinX(), (float) bb.getMaxY(), (float) bb.getMinZ());

            bufferBuilder.end();
        }
    }
}
