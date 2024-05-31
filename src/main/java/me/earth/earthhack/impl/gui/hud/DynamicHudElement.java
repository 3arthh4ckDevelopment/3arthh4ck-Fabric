package me.earth.earthhack.impl.gui.hud;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.render.Render2DUtil;

public abstract class DynamicHudElement extends HudElement {

    private final Setting<textDirectionX> alignmentPos =
            register(new EnumSetting<>("Alignment", textDirectionX.Smart));
    private final Setting<TextDirectionY> textDirection =
            register(new EnumSetting<>("Direction", TextDirectionY.Smart));

    public DynamicHudElement(String name, String description, HudCategory category, float x, float y) {
        super(name, description, category, x, y);
    }

    public float simpleCalcX(float completeValue) {
        return directionX() == textDirectionX.Center ? completeValue / 2.0f : (directionX() == textDirectionX.Right ? 0 : completeValue);
    }

    public textDirectionX directionX() {
        return alignmentPos.getValue() == textDirectionX.Smart ? SmartDirectionX() : alignmentPos.getValue();
    }

    public TextDirectionY directionY() {
        return textDirection.getValue() == TextDirectionY.Smart ? SmartDirectionY() : textDirection.getValue();
    }

    private textDirectionX SmartDirectionX() {
        float center = (float) ((Render2DUtil.getScreenWidth() / Render2DUtil.getScreenScale() - getWidth()) / 2.0f);
        if (getX() > center - 60 && getX() < center + 60)
            return textDirectionX.Center;
        else if (getX() > center)
            return textDirectionX.Left;
        return textDirectionX.Right;
    }

    private TextDirectionY SmartDirectionY() {
        float center = Render2DUtil.getScreenHeight() / 2.0f + getHeight() / 2;
        return getY() > center ? TextDirectionY.BottomToTop : TextDirectionY.TopToBottom;
    }

    public enum textDirectionX {
        Smart,
        Center,
        Right,
        Left
    }

    public enum TextDirectionY {
        Smart,
        TopToBottom,
        BottomToTop
    }

}
