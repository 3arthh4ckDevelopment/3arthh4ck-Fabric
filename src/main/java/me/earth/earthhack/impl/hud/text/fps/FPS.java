package me.earth.earthhack.impl.hud.text.fps;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class FPS extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("Identifier", "FPS"));
    private final Setting<Boolean> fastUpdate =
            register(new BooleanSetting("Fast", false)
                    .setComplexity(Complexity.Dev)); // ^^ todo fix


    private String fps = name.getValue();

    private void render(DrawContext context) {
        if (mc.player != null && mc.world != null)
            fps = name.getValue() + " " + TextColor.GRAY + (fastUpdate.getValue() ? ((IMinecraftClient) mc).earthhack$getFpsCounter() : mc.getCurrentFps());
        HudRenderUtil.renderText(context, fps, getX(), getY());
    }

    public FPS() {
        super("FPS", HudCategory.Text, 50, 20);
        this.setData(new SimpleHudData(this, "Displays your FPS"));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(fps);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
