package me.earth.earthhack.impl.util.helpers.render;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

// TODO: gradient?
public class BlockESPModule extends ColorModule
{
    public final ColorSetting outline =
            register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    public final Setting<Float> lineWidth =
            register(new NumberSetting<>("LineWidth", 1.5f, 0.0f, 10.0f));
    public final Setting<Float> height =
            register(new NumberSetting<>("ESP-Height", 1.0f, -1.0f, 1.0f));

    protected IAxisESP esp = new BlockESPBuilder()
            .withColor(color)
            .withOutlineColor(outline)
            .withLineWidth(lineWidth)
            .build();

    public BlockESPModule(String name, Category category)
    {
        super(name, category);
        super.color.setValue(new Color(255, 255, 255, 76));
    }

    public void renderPos(MatrixStack matrix, BlockPos pos)
    {
        esp.render(matrix, Interpolation.interpolatePos(pos, height.getValue()));
    }

    public void renderAxis(MatrixStack matrix, Box bb)
    {
        esp.render(matrix, Interpolation.interpolateAxis(bb));
    }

    /**
     * @param bb the axis to render, needs to interpolated already.
     */
    public void renderInterpAxis(MatrixStack matrix, Box bb)
    {
        esp.render(matrix, bb);
    }

}
