package me.earth.earthhack.impl.hud.impl.text.tps;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class Tps extends HudElement {

    private final Setting<Boolean> currentTps =
            register(new BooleanSetting("CurrentTps", false));
    private final Setting<String> name =
            register(new StringSetting("Name", "TPS"));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));
    private String tps = name.getValue();

    private void render(DrawContext context) {
        if (mc.player != null) {
            tps = name.getValue() + " " + TextColor.GRAY + MathUtil.round(Managers.TPS.getTps(), 2);
            if (currentTps.getValue())
                tps += actualBracket()[0] + MathUtil.round(Managers.TPS.getCurrentTps(), 2) + actualBracket()[1];
        }
        HudRenderUtil.renderText(context, tps, getX(), getY());
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[0] + HudRenderUtil.bracketsTextColor(), HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Tps() {
        super("TPS", HudCategory.Text, 80, 280);
        this.setData(new SimpleHudData(this, "Displays the server's TPS."));
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
        return Managers.TEXT.getStringWidth(tps);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
