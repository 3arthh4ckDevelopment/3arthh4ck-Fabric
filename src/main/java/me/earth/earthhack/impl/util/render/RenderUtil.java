package me.earth.earthhack.impl.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.mojang.blaze3d.systems.RenderSystem.disableBlend;
import static org.lwjgl.opengl.GL11.*;


// TODO: One Mutable.BlockPos for the MainThread
// TODO: One Mutable Box (aabb) for the MainThread
// TODO: One Frustum for the MainThread
//  That way we don't need to instantiate neither of them at all
//  Which could actually save quite the cost, since we render often
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

    // private static final Frustum FRUSTUM = new Frustum(null);

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
    
    public static void genOpenGlBuffers()
    {
      //  if (OpenGlHelper.areVbosSupported()) // todo
      //  {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            Box bb = new Box(0, 0, 0, 1, 1, 1); // one block
            // filled box
            bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ);

            bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ);

            bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ);

            bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ);

            bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ);

            bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ);
            // contains all vertexition data for drawing a filled cube
            bufferBuilder.end();
            bufferBuilder.reset();
            ByteBuffer byteBuffer = bufferBuilder.end().getVertexBuffer();
            // BLOCK_FILL_BUFFER.bindBuffer();
            // TODO: BLOCK_FILL_BUFFER.upload(byteBuffer);
            // BLOCK_FILL_BUFFER.unbindBuffer();

            bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.maxZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.minY, bb.minZ);
            bufferBuilder.vertex(bb.maxX, bb.maxY, bb.minZ);
            bufferBuilder.vertex(bb.minX, bb.maxY, bb.minZ);
            bufferBuilder.end();
            bufferBuilder.reset();
            ByteBuffer outlineBuffer = bufferBuilder.end().getVertexBuffer();
            // BLOCK_OUTLINE_BUFFER.bind();
            // TODO: BLOCK_OUTLINE_BUFFER.upload(outlineBuffer);
            // BLOCK_OUTLINE_BUFFER.unbind();

      //  }
      //  else
      //  {
            Earthhack.getLogger().info("VBOs not supported, skipping.");
      //  }
    }

    public static void renderBox(double x, double y, double z)
    {
        startRender();
        BLOCK_FILL_BUFFER.bind();
        double viewX = mc.gameRenderer.getCamera().getPos().x;
        double viewY = mc.gameRenderer.getCamera().getPos().y;
        double viewZ = mc.gameRenderer.getCamera().getPos().z;
        glTranslated(x - viewX, y - viewY, z - viewZ);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 12, 0);
        BLOCK_FILL_BUFFER.draw(/*GL_QUADS*/);
        BLOCK_FILL_BUFFER.unbind();
        glDisableClientState(GL_VERTEX_ARRAY);
        glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        endRender();
    }

    public static void drawBox(MatrixStack matrix, Box bb, Color color)
    {
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        color(color);
        fillBox(matrix, bb, color.getRGB());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        disableBlend();
    }

    public static void renderBox(MatrixStack matrix,
                                 Box bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth)
    {
        startRender();
        drawOutline(matrix, bb, lineWidth, outLineColor);
        endRender();
//        startRender();
//        drawBox(matrix, bb, color);
//        endRender();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void renderBox(MatrixStack matrix, BlockPos vertex, Color color, float height)
    {
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);

        Box bb = Interpolation.interpolatePos(vertex, height);
        startRender();
        drawOutline(matrix, bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        startRender();
        drawBox(matrix, bb, boxColor);
        endRender();

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glPopAttrib();
        glPopMatrix();
    }

    public static void drawOutline(MatrixStack matrix, Box bb, float lineWidth)
    {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(lineWidth);
        fillOutline(matrix, bb, 0);
        glLineWidth(1.0f);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
    }
    public static void drawOutline(MatrixStack matrix, Box bb, float lineWidth, Color color)
    {


        RenderSystem.lineWidth(lineWidth);
        fillOutline(matrix, bb, color.getRGB());


    }

    public static void fillBox(MatrixStack matrix, Box boundingBox, int color)
    {
        if (boundingBox != null)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            Matrix4f posMatrix = matrix.peek().getPositionMatrix();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.vertex(posMatrix, (float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ).color(color).next();
            bufferBuilder.end();

            tessellator.draw();
        }
    }

    public static void fillOutline(MatrixStack matrix, Box bb, int color)
    {
        if (bb != null)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();


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
        RenderSystem.setShaderColor(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public static void color(int color)
    {
        float[] color4f = ColorUtil.toArray(color);
        glColor4f(color4f[0], color4f[1], color4f[2], color4f[3]);
    }

    public static void color(float r, float g, float b, float a)
    {
        glColor4f(r, g, b, a);
    }

    public static void scissor(float x, float y, float x1, float y1)
    {
        wnd = mc.getWindow();
        double scale = wnd.getScaleFactor();
        glScissor((int) (x * scale),
                (int) ((wnd.getScaledHeight() - y1) * scale),
                (int)((x1 - x) * scale),
                (int)((y1 - y) * scale));
    }

    public static void startRender()
    {
        RenderSystem.enableBlend();
        //RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
    }

    public static void endRender()
    {
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        //RenderSystem.enableCull();
        disableBlend();
    }

    public static boolean mouseWithinBounds(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return (mouseX >= x && mouseX <= (x + width)) && (mouseY >= y && mouseY <= (y + height));
    }

}
