package me.earth.earthhack.impl.hud.text.tps;

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

public class Tps extends HudElement {

    private final Setting<Boolean> currentTps =
            register(new BooleanSetting("CurrentTps", false));
    private final Setting<String> name =
            register(new StringSetting("Name", "TPS"));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));
    private String tps = name.getValue();

    private void render() {
        if (mc.player != null) {
            tps = name.getValue() + " " + TextColor.GRAY + MathUtil.round(Managers.TPS.getTps(), 2);
            if (currentTps.getValue())
                tps += actualBracket()[0] + MathUtil.round(Managers.TPS.getCurrentTps(), 2) + actualBracket()[1];
        }
        HudRenderUtil.renderText(tps, getX(), getY());
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[0] + HudRenderUtil.BracketsTextColor(), HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Tps() {
        super("TPS", HudCategory.Text, 80, 280);
        this.setData(new SimpleHudData(this, "Displays the server's TPS."));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
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
