package me.earth.earthhack.impl.hud.text.binds;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.gui.hud.DynamicHudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Binds extends DynamicHudElement {

    private final Setting<Boolean> onlyCombat =
            register(new BooleanSetting("OnlyCombat", true));
    private final Setting<Boolean> onlyEnabled =
            register(new BooleanSetting("onlyEnabled", false));
    private final Setting<Boolean> showStatus =
            register(new BooleanSetting("ShowStatus", true));
    private final Setting<TextColor> enabledColor =
            register(new EnumSetting<>("EnabledColor", TextColor.Green));
    private final Setting<TextColor> disabledColor =
            register(new EnumSetting<>("DisabledColor", TextColor.Red));
    private final Setting<TextColor> bindColor =
            register(new EnumSetting<>("DisabledColor", TextColor.White));
    private final Setting<Boolean> showBind =
            register(new BooleanSetting("ShowBind", true)); // idk it could be something useful ig?
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));
    private final Setting<Integer> textOffset =
            register(new NumberSetting<>("Offset", 2, 0, 10));

    int counter = 0;
    List<Module> modules = new ArrayList<>();

    private void render(DrawContext context) {
        float moduleOffset = Managers.TEXT.getStringHeightI() + textOffset.getValue();
        if (mc.player != null && mc.world != null) {
            modules.clear();

            Managers.MODULES.forEach(module -> {
               if (!Objects.equals(module.getBind(), Bind.none()) && module.getBind().getKey() > 0) {
                   if (onlyCombat.getValue()) {
                       if (module.getCategory() == Category.Combat) {
                           if (onlyEnabled.getValue()) {
                               if (module.isEnabled()) {
                                   modules.add(module);
                               }
                           } else {
                               modules.add(module);
                           }
                       }
                   } else {
                       modules.add(module);
                   }
               }
            });
        }

        counter = modules.size();
        float yPos = getY();
        if (directionV() == TextDirectionV.BottomToTop)
            yPos += moduleOffset * (modules.size() + 1);
        for (Module m : modules) {
            HudRenderUtil.renderText(context, (showStatus.getValue() ? (m.isEnabled() ? enabledColor.getValue().getColor() : disabledColor.getValue().getColor()) : "") + m.getDisplayName() + TextColor.GRAY + " " + (showBind.getValue() ? actualBracket()[0] + bindColor.getValue().getColor() + Keyboard.getKeyName(m.getBind().getKey()) + actualBracket()[1] : ""),
                    getX() - simpleCalcH(Managers.TEXT.getStringWidth(m.getDisplayName() + actualBracket()[0] + Keyboard.getKeyName(m.getBind().getKey()) + actualBracket()[1])),
                    yPos);
            if (directionV() == TextDirectionV.BottomToTop)
                yPos -= moduleOffset;
            else
                yPos += moduleOffset;
        }
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[0] + HudRenderUtil.bracketsTextColor(), HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]" };
    }

    public Binds() {
        super("Binds", HudCategory.Text, 300, 300);
        this.setData(new SimpleHudData(this, "Displays the binded modules."));
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
        return 100.0f;
    }

    @Override
    public float getHeight() {
        return (Managers.TEXT.getStringHeight() + textOffset.getValue()) * counter;
    }

}
