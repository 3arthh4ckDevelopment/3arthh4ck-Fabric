package me.earth.earthhack.impl.hud.text.direction;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class Direction extends HudElement {

    private final Setting<Boolean> name =
            register(new BooleanSetting("Name", true));
    private final Setting<Boolean> symbol =
            register(new BooleanSetting("Symbol", true));

    private static String text = "";

    protected void onRender(DrawContext context) {
        text = getDirection4D(name.getValue(), symbol.getValue());
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public String getDirection4D(boolean name, boolean symbol) {
        String nameValue, symbolValue;
        switch (RotationUtil.getDirection4D())
        {
            case 0:
                nameValue = "South";
                symbolValue = symbolBuilder("+Z", name);
                break;
            case 1:
                nameValue = "West";
                symbolValue = symbolBuilder("-X", name);
                break;
            case 2:
                nameValue = "North";
                symbolValue = symbolBuilder("-Z", name);
                break;
            case 3:
            default:
                nameValue = "East";
                symbolValue = symbolBuilder("+X", name);
                break;
        }
        return (name ? nameValue :  "") + (name && symbol ? " " : "") + (symbol ? symbolValue : "");
    }

    private String symbolBuilder(String dir, boolean name) {
        if (!name)
            return dir;
        return surroundWithBrackets(TextColor.WHITE + dir + TextColor.GRAY);
    }

    public Direction() {
        super("Direction", "Displays the direction you're currently facing.", HudCategory.Text, 180, 90);
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text.trim());
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }
}
