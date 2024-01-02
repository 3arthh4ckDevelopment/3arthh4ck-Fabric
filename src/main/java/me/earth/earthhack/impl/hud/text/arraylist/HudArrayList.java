package me.earth.earthhack.impl.hud.text.arraylist;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.hud.DynamicHudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.modules.PbModule;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

public class HudArrayList extends DynamicHudElement {

    protected final Setting<Modules> moduleRender =
            register(new EnumSetting<>("Modules", Modules.Length));
    private final Setting<Integer> textOffset =
            register(new NumberSetting<>("Offset", 2, 0, 10));
    private final Setting<Boolean> animations =
            register(new BooleanSetting("Animations", false));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));

    protected final Map<Module, HudArrayEntry> arrayEntries = new HashMap<>();
    protected static final Map<Module, HudArrayEntry> removeEntries = new HashMap<>();
    protected final List<Map.Entry<String, Module>> modules = new java.util.ArrayList<>();
    protected Map<Module, HudArrayEntry> arrayEntriesSorted;

    private void render() {
        GL11.glPushMatrix();
        float xPos = getX() + simpleCalcH(getWidth());
        float yPos = (directionV() == TextDirectionV.BottomToTop ? getY() - Managers.TEXT.getStringHeight() * 4 : getY());
        float moduleOffset = Managers.TEXT.getStringHeightI() + textOffset.getValue();
        if (animations.getValue()) {
            for (Map.Entry<String, Module> module : modules) {
                if (isArrayMember(module.getValue()))
                    continue;
                getArrayEntries().put(module.getValue(), new HudArrayEntry(module.getValue()));
                if (!(module.getValue() instanceof PbModule)) {
                    getArrayEntries()
                            .entrySet()
                            .removeIf(m -> m.getKey() instanceof PbModule
                                    && Objects.equals(
                                    ((PbModule) m.getKey()).getModule(),
                                    module.getValue()));
                }
            }

            if (moduleRender.getValue() == Modules.Length) {
                arrayEntriesSorted = getArrayEntries().entrySet().stream().sorted(Comparator.comparingDouble(entry -> Managers.TEXT.getStringWidth(getHudName(entry.getKey())) * -1)).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
            } else {
                arrayEntriesSorted = getArrayEntries().entrySet().stream().sorted(Comparator.comparing(entry -> getHudName(entry.getKey()))).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
            }
            for (HudArrayEntry arrayEntry : arrayEntriesSorted.values()) {
                arrayEntry.drawArrayEntry(xPos, yPos);
                yPos += moduleOffset;
            }
            getRemoveEntries().forEach((key, value) -> getArrayEntries().remove(key));
            getRemoveEntries().clear();
        } else {
            if (directionV() == TextDirectionV.BottomToTop)
                yPos += moduleOffset * (modules.size() + 1);
            for (Map.Entry<String, Module> module : modules) {
                HudRenderUtil.renderText(module.getKey(), xPos - simpleCalcH(RENDERER.getStringWidth(module.getKey())), yPos);
                if (directionV() == TextDirectionV.BottomToTop)
                    yPos -= moduleOffset;
                else
                    yPos += moduleOffset;
            }
        }
        GL11.glPopMatrix();
    }

    public String getHudName(Module module)
    {
        return module.getDisplayName()
                + (module.getDisplayInfo() == null
                || module.isHidden() == Hidden.Info
                ? ""
                : TextColor.GRAY
                + actualBracket()[0] + TextColor.WHITE
                + module.getDisplayInfo()
                + TextColor.GRAY + actualBracket()[1]);
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ " " + HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[0] + HudRenderUtil.BracketsTextColor(), HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ " " + TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Map<Module, HudArrayEntry> getArrayEntries() {
        return arrayEntries;
    }

    public static Map<Module, HudArrayEntry> getRemoveEntries() {
        return removeEntries;
    }

    public HudArrayList() {
        super("ModuleList", HudCategory.Text, 200, 200);
        this.listeners.add(new ListenerPostKey(this));
        this.setData(new SimpleHudData(this, "Displays enabled modules."));
    }

    protected boolean isArrayMember(Module module) {
        return getArrayEntries().containsKey(module)
                || module instanceof PbModule
                && getArrayEntries().containsKey(((PbModule) module)
                .getModule());
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
        return 115.0f;
    }

    @Override
    public float getHeight() {
        if (!modules.isEmpty())
            return (Managers.TEXT.getStringHeight() + textOffset.getValue()) * modules.size();
        else
            return Managers.TEXT.getStringHeight();
    }

}
