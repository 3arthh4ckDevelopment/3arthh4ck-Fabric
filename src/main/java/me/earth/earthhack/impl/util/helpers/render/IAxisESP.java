package me.earth.earthhack.impl.util.helpers.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

/** {@link BlockESPBuilder} */
@FunctionalInterface
public interface IAxisESP
{
    /** Draws a BlockESP at the given Box. */
    void render(MatrixStack matrix, Box bb);

}