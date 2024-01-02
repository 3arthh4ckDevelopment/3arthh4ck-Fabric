package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.render.Render2DUtil;

public abstract class DynamicHudElement extends HudElement {
    public DynamicHudElement(String name, HudCategory category, float x, float y) {
        super(name, category, x, y);
    }

    private final Setting<TextDirectionH> alignmentPos =
            register(new EnumSetting<>("Alignment", TextDirectionH.Smart));
    private final Setting<TextDirectionV> textDirection =
            register(new EnumSetting<>("Direction", TextDirectionV.Smart));

    public float simpleCalcH(float completeValue) {
        return (directionH() == TextDirectionH.Center ? completeValue / 2.0f : (directionH() == TextDirectionH.Right ? 0 : completeValue));
    }

    public TextDirectionH directionH() {
        if (alignmentPos.getValue() == TextDirectionH.Smart)
            return SmartDirectionH();
        else
            return alignmentPos.getValue();
    }

    public TextDirectionV directionV() {
        if (textDirection.getValue() == TextDirectionV.Smart)
            return SmartDirectionV();
        else
            return textDirection.getValue();
    }

    private TextDirectionH SmartDirectionH() {
        float center = (float) (Render2DUtil.getScreenWidth() / 2 / Render2DUtil.getScreenScale());
        if (getX() > center - 60 && getX() < center + 60)
            return TextDirectionH.Center;
        else if (getX() > center)
            return TextDirectionH.Left;
        else
            return TextDirectionH.Right;
    }

    private TextDirectionV SmartDirectionV() {
        float center = Render2DUtil.getScreenHeight() / 2.0f + getHeight() / 2;
        if (getY() > center)
            return TextDirectionV.BottomToTop;
        else
            return TextDirectionV.TopToBottom;
    }


    public enum TextDirectionH {
        Smart,
        Center,
        Right,
        Left
    }

    public enum TextDirectionV {
        Smart,
        TopToBottom,
        BottomToTop
    }

}
