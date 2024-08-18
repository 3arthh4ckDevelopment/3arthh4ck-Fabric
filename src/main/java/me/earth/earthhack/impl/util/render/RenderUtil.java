package me.earth.earthhack.impl.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// TODO: One Mutable.BlockPos for the MainThread
// TODO: One Mutable Box (aabb) for the MainThread
// TODO: One Frustum for the MainThread
//  That way we don't need to instantiate neither of them at all
//  Which could actually save quite the cost, since we render often
// TODO: MSAAFramebuffer
//  Then rendering won't be pixelated and will look like it did in 1.12
// TODO: maybe just rewrite this util, most of this shit is unnecessary
//  and WILL cause the game to crash
public class RenderUtil implements Globals {
    private static Window wnd;
    private final static GlShader IMAGE_SHADER = GlShader.createShader("image");
    public final static GlShader BLUR_SHADER = GlShader.createShader("blur");
    // todo vertexbuffers
    private static final VertexBuffer BLOCK_FILL_BUFFER = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private static final VertexBuffer BLOCK_OUTLINE_BUFFER = new VertexBuffer(VertexBuffer.Usage.STATIC);

    public final static FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    public final static IntBuffer viewport = BufferUtils.createIntBuffer(16);
    public final static FloatBuffer viewportFloat = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer projection = BufferUtils.createFloatBuffer(16);

    public static Frustum FRUSTUM;

    static
    {
        wnd = mc.getWindow();
        // genOpenGlBuffers();
    }

    public static void updateMatrices()
    {
        // glGetFloat(GL_MODELVIEW_MATRIX, modelView);
        // glGetFloat(GL_PROJECTION_MATRIX, projection);
        // glGetInteger(GL_VIEWPORT, viewport);
        // GLUProjection.getInstance().updateMatrices(viewport, modelView, projection,
        //         (float) wnd.getScaledWidth() / (float) mc.getWindow().getWidth(),
        //         (float) wnd.getScaledHeight() / (float) mc.getWindow().getHeight());
    }
    
    // TODO: perhaps programmatically gen vbos when settings in block esp modules change to support gradient rendering and differed boxes?
    // TODO: vbos for planes + streamline code for

    public static Entity getEntity() {
        return mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity();
    }

    public static void genOpenGlBuffers()
    {
      //  if (OpenGlHelper.areVbosSupported()) // todo
      //  {
            BufferBuilder bufferBuilder =Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            Box bb = new Box(0, 0, 0, 1, 1, 1); // one block
            // filled box
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ);

            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ);

            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ);

            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ);

            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ);

            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
            // contains all vertexition data for drawing a filled cube
            bufferBuilder.end();

            // ByteBuffer byteBuffer = bufferBuilder.end().getVertexBuffer();
            // BLOCK_FILL_BUFFER.bindBuffer();
            // TODO: BLOCK_FILL_BUFFER.upload(byteBuffer);
            // BLOCK_FILL_BUFFER.unbindBuffer();

            bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.minY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.maxX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.vertex((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
            bufferBuilder.end();

            // ByteBuffer outlineBuffer = bufferBuilder.end().getVertexBuffer();
            // BLOCK_OUTLINE_BUFFER.bind();
            // TODO: BLOCK_OUTLINE_BUFFER.upload(outlineBuffer);
            // BLOCK_OUTLINE_BUFFER.unbind();

      //  }
      //  else
      //  {
            Earthhack.getLogger().info("VBOs not supported, skipping.");
      //  }
    }

    public static void drawBox(MatrixStack matrix, Box bb, Color color)
    {
        startRender();
        fillBox(matrix, bb, color.getRGB());
        endRender();
    }

    public static void renderBox(MatrixStack matrix,
                                 BlockPos pos,
                                 Color color,
                                 float height)
    {
        Box bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(matrix, bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        startRender();
        drawBox(matrix, bb, boxColor);
        endRender();
    }

    public static void renderBox(MatrixStack matrix, BlockPos pos,
                                 Color color,
                                 float height,
                                 int boxAlpha)
    {
        Box bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(matrix, bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha);
        startRender();
        drawBox(matrix, bb, boxColor);
        endRender();
    }

    public static void renderBox(MatrixStack matrix,
                                 Box bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth)
    {
        RenderSystem.lineWidth(lineWidth);
        drawOutline(matrix, bb, lineWidth, outLineColor);
        RenderSystem.lineWidth(1.0f);
        drawBox(matrix, bb, color);
    }

    public static void drawOutline(MatrixStack matrix, Box bb, float lineWidth, Color color)
    {
        startRender();
        RenderSystem.lineWidth(lineWidth);
        fillOutline(matrix, bb, color);
        endRender();
    }

    public static void fillBox(MatrixStack matrix, Box bb, int color)
    {
        if (bb != null) {
            Matrix4f posMatrix = matrix.peek().getPositionMatrix();

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(color);

            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(color);

            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(color);

            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(color);

            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(color);

            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ).color(color);
            bufferBuilder.vertex(posMatrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ).color(color);

            bufferBuilder.end();
        }
    }

    public static void fillOutline(MatrixStack matrix, Box bb, Color color)
    {
        if (bb != null)
        {
            float alpha = color.getAlpha() / 255.0F;
            float red = color.getRed() / 255.0F;
            float green = color.getGreen() / 255.0F;
            float blue = color.getBlue() / 255.0F;

            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            WorldRenderer.drawBox(matrix, bufferBuilder, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);

            bufferBuilder.end();
        }
    }

    public static Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    public static void color(Color color)
    {
        color(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public static void color(int color)
    {
        float[] color4f = ColorUtil.toArray(color);
        RenderSystem.setShaderColor(color4f[0], color4f[1], color4f[2], color4f[3]);
    }

    public static void color(float r, float g, float b, float a)
    {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    public static void startRender()
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }

    public static void endRender()
    {
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    public static boolean isInFrustum(Box bb)
    {
        Entity renderEntity = getEntity();
        if (renderEntity == null)
        {
            return false;
        }

        Interpolation.setFrustum(FRUSTUM, renderEntity);
        return FRUSTUM.isVisible(bb);
    }

}
