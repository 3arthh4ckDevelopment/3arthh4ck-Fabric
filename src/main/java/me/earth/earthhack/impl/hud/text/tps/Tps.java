package me.earth.earthhack.impl.hud.text.tps;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class Tps extends HudElement {

    private final Setting<Boolean> currentTps =
            register(new BooleanSetting("CurrentTps", false));
    private final Setting<String> name =
            register(new StringSetting("Name", "TPS"));

    private String text = "";

    protected void onRender(DrawContext context) {
        text = name.getValue() + " " + TextColor.GRAY + MathUtil.round(Managers.TPS.getTps(), 2);
        if (currentTps.getValue())
            text += surroundWithBrackets(String.valueOf(MathUtil.round(Managers.TPS.getCurrentTps(), 2)));
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public Tps() {
        super("TPS", "Displays the server's TPS.", HudCategory.Text, 80, 280);
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
