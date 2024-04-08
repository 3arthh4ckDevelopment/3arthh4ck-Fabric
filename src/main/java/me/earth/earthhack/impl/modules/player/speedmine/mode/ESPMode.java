package me.earth.earthhack.impl.modules.player.speedmine.mode;

import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

import java.awt.*;

public enum ESPMode
{
    None()
        {
            @Override
            public void drawEsp(MatrixStack matrix, Speedmine module, Box bb, float damage)
            {
                /* None means no ESP. */
            }
        },
    Outline()
        {
            @Override
            public void drawEsp(MatrixStack matrix, Speedmine module, Box bb, float damage)
            {
                RenderUtil.startRender();
                float red   = 255 - 255 * damage;
                float green = 255 * damage;
                RenderUtil.drawOutline(matrix, bb, 1.5F, new Color((int) red, (int) green, 0, module.getOutlineAlpha()));
                RenderUtil.endRender();
            }
        },
    Block()
        {
            @Override
            public void drawEsp(MatrixStack matrix, Speedmine module, Box bb, float damage)
            {
                RenderUtil.startRender();
                float red   = 255 - 255 * damage;
                float green = 255 * damage;
                RenderUtil.drawBox(matrix, bb, new Color((int) red, (int) green, 0, module.getBlockAlpha()));
                RenderUtil.endRender();
            }
        },
    Box()
        {
            @Override
            public void drawEsp(MatrixStack matrix, Speedmine module, Box bb, float damage)
            {
                Outline.drawEsp(matrix, module, bb, damage);
                Block.drawEsp(matrix, module, bb, damage);
            }
        };

    public abstract void drawEsp(MatrixStack matrix, Speedmine module, Box bb, float damage);

}
