package me.earth.earthhack.impl.util.render.image;

import com.mojang.blaze3d.platform.TextureUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.render.ITextureManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;

public class ImageUtil implements Globals {
    public static BufferedImage createFlipped(BufferedImage image)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    public static BufferedImage createTransformed(BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public static BufferedImage bufferedImageFromFile(File file) throws IOException
    {
        Image image = ImageIO.read(file);
        String format = file.getName().split("\\.")[1];
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    // hashes image and caches image with hash so that performance of gif rendering is improved vastly
    public static EfficientTexture cacheBufferedImage(BufferedImage image, String format, String name) throws NoSuchAlgorithmException, IOException {
        /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        image.getData().getDataBuffer().
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);*/
        EfficientTexture texture = new EfficientTexture(image);
        Identifier location = ((ITextureManager) mc.getTextureManager()).getEfficientTextureResourceLocation(name, texture);
        loadTexture(location, texture);
        return texture;
    }

    public static void loadTexture(Identifier loc, AbstractTexture tex) {
        try {
            Method method = mc.getTextureManager().getClass().getMethod(
                    "loadTexture",
                    Identifier.class,
                    AbstractTexture.class);

            boolean access = method.isAccessible();
            method.setAccessible(true);
            assert loc != null;
            assert tex != null;
            method.invoke(method, loc, tex);
            method.setAccessible(access);
        } catch(Exception e) {
            if(e instanceof NoSuchMethodException)
                Earthhack.getLogger().error("Unable to load texture: " + loc.getPath());
            else
                e.printStackTrace();
        }
    }

    // generates md5 hash to be used as name for image to improve performance when rendering gifs
    public static void bindImage(BufferedImage bufferedImage) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String format = getImageFormat(bufferedImage);
        if (format == null) throw new IOException();
        ImageIO.write(bufferedImage, format, outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);
        NativeImage nat = NativeImage.read(TextureUtil.readResource(new ByteArrayInputStream(data)));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(nat);
        Identifier location = mc.getTextureManager().registerDynamicTexture(name, texture);
        mc.getTextureManager().bindTexture(location);
    }

    public static void bindImage(BufferedImage bufferedImage, String format) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (format == null) throw new IOException();
        ImageIO.write(bufferedImage, format, outputStream);
        byte[] data = outputStream.toByteArray();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data);
        byte[] hash = md.digest();
        String name = new String(hash);
        NativeImage nat = NativeImage.read(TextureUtil.readResource(new ByteArrayInputStream(data)));
        NativeImageBackedTexture texture = new NativeImageBackedTexture(nat);
        Identifier location = mc.getTextureManager().registerDynamicTexture(name, texture);
        mc.getTextureManager().bindTexture(location);
    }

    public static String getImageFormat(BufferedImage image) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(image);

        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
        if (!iter.hasNext()) {
            return null;
        }
        ImageReader reader = iter.next();
        ImageReadParam param = reader.getDefaultReadParam();
        reader.setInput(stream, true, true);
        BufferedImage bi;
        try {
            bi = reader.read(0, param);
            return reader.getFormatName();
        } finally {
            reader.dispose();
            stream.close();
        }
    }

    public static int loadImage(BufferedImage image) {
        ByteBuffer buffer = bufferedImageToByteBuffer(image);
        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return texture;
    }

    public static ByteBuffer bufferedImageToByteBuffer(BufferedImage image)
    {
        ByteBuffer byteBuffer;
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();

        if (dataBuffer instanceof DataBufferByte)
        {
            byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        }
        else if (dataBuffer instanceof DataBufferUShort)
        {
            short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferShort)
        {
            short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        }
        else if (dataBuffer instanceof DataBufferInt)
        {
            int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        }
        else
        {
            throw new IllegalArgumentException("Not implemented for data buffer type: " + dataBuffer.getClass());
        }
        return null;
    }

    public static ByteBuffer readImageToBuffer(InputStream image) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(image);
        int[] pixelIndex = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(pixelIndex.length * 4);
        for (int i : pixelIndex) {
            bytebuffer.putInt(i << 8 | (i >> 24 & 255));
        }
        bytebuffer.flip();
        return bytebuffer;
    }
}
