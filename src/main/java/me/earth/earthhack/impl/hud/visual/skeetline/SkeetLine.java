package me.earth.earthhack.impl.hud.visual.skeetline;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.hud.HudPositionOffsetManager;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class SkeetLine extends HudElement {
    private final Setting<Color> color1 =
            register(new ColorSetting("Color1", new Color(87, 9, 241)));
    private final Setting<Color> color2 =
            register(new ColorSetting("Color2", new Color(10, 30, 245, 255)));
    private final Setting<Color> color3 =
            register(new ColorSetting("Color3", new Color(0, 255, 216)));
    private final Setting<Float> height =
            register(new NumberSetting<>("SkeetLineHeight", 1.3f, 0.5f, 3.0f));
    private final Setting<Float> speed =
            register(new NumberSetting<>("Speed", 1.0f, 0.1f, 5.0f));
    private final Setting<Integer> colorsNumber =
            register(new NumberSetting<>("ColorsNumber", 3, 1, 3));
    private final Setting<Direction> direction =
            register(new EnumSetting<>("Direction", Direction.LEFT_TO_RIGHT));

    private float time = 0;

    protected void onRender(DrawContext context) {
        int endScreen = mc.getWindow().getScaledWidth();
        int midScreen = endScreen / 2;

        if (colorsNumber.getValue() == 0) {
            Render2DUtil.drawGradientRect(context.getMatrices(), 0, 0, midScreen, height.getValue(), true, color1.getValue().getRGB(), color1.getValue().getRGB());
            return;
        }

        float offset = time % (endScreen + (colorsNumber.getValue() == 3 ? midScreen : endScreen)) * (direction.getValue() == Direction.LEFT_TO_RIGHT ? 1 : -1);
        float sectionSize = colorsNumber.getValue() == 3 ? midScreen : endScreen;
        Color c1 = color1.getValue();
        Color c2 = color2.getValue();
        Color c3 = color3.getValue();

        float x = -endScreen - sectionSize;
        for (int i = 0; i < 8; i++) {
            Render2DUtil.drawGradientRect(context.getMatrices(), x + offset, 0, x + sectionSize + offset, height.getValue(), true, c1.getRGB(), c2.getRGB());
            x += sectionSize;

            if (colorsNumber.getValue() == 3) {
                Color temp = c1;
                c1 = c2;
                c2 = c3;
                c3 = temp;
            } else {
                Color temp = c1;
                c1 = c2;
                c2 = temp;
            }
        }
        time += speed.getValue();
    }

    public SkeetLine() {
        super("SkeetLine", "Displays the SkeetLine.", HudCategory.Visual, -30, -30);
    }

    @Override
    public void onEnable() {
        HudPositionOffsetManager.setY(height.getValue());
    }

    @Override
    public void onDisable() {
        HudPositionOffsetManager.setY(0);
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    private enum Direction {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }
}
