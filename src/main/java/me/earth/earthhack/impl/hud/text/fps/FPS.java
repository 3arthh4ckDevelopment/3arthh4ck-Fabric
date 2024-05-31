package me.earth.earthhack.impl.hud.text.fps;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class FPS extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("CustomName", "FPS"));
    private final Setting<Boolean> fastUpdate =
            register(new BooleanSetting("Fast", false)
                    .setComplexity(Complexity.Dev)); // ^^ todo fix

    private String text = name.getValue();

    protected void onRender(DrawContext context) {
        text = name.getValue() + " " + TextColor.GRAY + (fastUpdate.getValue() ? ((IMinecraftClient) mc).earthhack$getFpsCounter() : mc.getCurrentFps());
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public FPS() {
        super("FPS", "Displays your FPS", HudCategory.Text, 50, 20);
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
