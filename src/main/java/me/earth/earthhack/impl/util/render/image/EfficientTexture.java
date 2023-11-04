package me.earth.earthhack.impl.util.render.image;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;

import java.awt.image.BufferedImage;

/**
 * Texture that does not store pixel data on the cpu.
 * Should be used when storing large amounts of textures is necessary.
 * Will be replaced when a more modern rendering pipeline is introduced for rendering stuff.
 * @author megyn
 */
public class EfficientTexture extends AbstractTexture
{
    private int[] textureData;
    /** width of this icon in pixels */
    private final int width;
    /** height of this icon in pixels */
    private final int height;

    public EfficientTexture(BufferedImage bufferedImage)
    {
        this(bufferedImage.getWidth(), bufferedImage.getHeight());
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.textureData, 0, bufferedImage.getWidth());
        this.updateEfficientTexture();
    }

    public EfficientTexture(int textureWidth, int textureHeight)
    {
        this.width = textureWidth;
        this.height = textureHeight;
        this.textureData = new int[textureWidth * textureHeight];
        TextureUtil.prepareImage(getGlId(), textureWidth, textureHeight);
    }

    @Override
    public void load(ResourceManager resourceManager)
    {

    }

    private void updateEfficientTexture()
    {
        // TextureUtil.loadTexture(this.getGlId(), this.textureData, this.width, this.height);
        this.bindTexture();
        textureData = new int[0];
    }

}
