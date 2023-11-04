package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RenderUtil implements Globals {
    private static Window wnd;

    private final static GlShader IMAGE_SHADER = GlShader.createShader("image");
    public final static GlShader BLUR_SHADER = GlShader.createShader("blur");

//    private static final VertexBuffer BLOCK_FILL_BUFFER = new VertexBuffer(VertexFormats.POSITION);
//    private static final VertexBuffer BLOCK_OUTLINE_BUFFER = new VertexBuffer(VertexFormats.POSITION);

    public final static FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    public final static IntBuffer viewport = BufferUtils.createIntBuffer(16);
    public final static FloatBuffer viewportFloat = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer projection = BufferUtils.createFloatBuffer(16);

    private static final Frustum FRUSTUM = new Frustum(null);




}
