package me.earth.earthhack.impl.hud.text.safetyhud;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class SafetyHud extends HudElement {

    private final Setting<TextColor> safeColor =
            register(new EnumSetting<>("SafeColor", TextColor.Green));
    private final Setting<TextColor> unsafeColor =
            register(new EnumSetting<>("UnsafeColor", TextColor.Red));
    private final Setting<String> name =
            register(new StringSetting("CustomName", ""));
    private final Setting<String> safeName =
            register(new StringSetting("SafeName", "SAFE"));
    private final Setting<String> unsafeName =
            register(new StringSetting("UnsafeName", "UNSAFE"));
    private final Setting<Boolean> showBrackets =
            register(new BooleanSetting("ShowBrackets", false));

    private String text = "";

    protected void onRender(DrawContext context) {
        String safetyStatus = Managers.SAFETY.isSafe() ? safeColor.getValue().getColor() + safeName.getValue() : unsafeColor.getValue().getColor() + unsafeName.getValue();
        text = name.getValue() + (showBrackets.getValue() ? surroundWithBrackets(safetyStatus) : safetyStatus);


        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public SafetyHud() {
        super("Safety", "Displays if you're safe or not.",  HudCategory.Visual, 30, 30);
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
