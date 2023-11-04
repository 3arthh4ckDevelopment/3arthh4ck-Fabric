package me.earth.earthhack.impl.core.ducks.render;

import me.earth.earthhack.impl.util.render.image.EfficientTexture;
import net.minecraft.util.Identifier;

public interface ITextureManager {
    Identifier getEfficientTextureResourceLocation(String name, EfficientTexture texture);
}
