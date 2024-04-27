package me.earth.earthhack.impl.hud.text.arraylist;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Hidden;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.gui.hud.DynamicHudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.modules.PbModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.stream.Collectors;

public class HudArrayList extends DynamicHudElement {

    protected final Setting<ModuleSorting> moduleRender =
            register(new EnumSetting<>("ModuleSorting", ModuleSorting.Length));
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

    private void render(DrawContext context) {
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

            if (moduleRender.getValue() == ModuleSorting.Length) {
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
                arrayEntry.drawArrayEntry(context, xPos, yPos);
                yPos += moduleOffset;
            }
            getRemoveEntries().forEach((key, value) -> getArrayEntries().remove(key));
            getRemoveEntries().clear();
        } else {
            if (directionV() == TextDirectionV.BottomToTop)
                yPos += moduleOffset * (modules.size() + 1);
            for (Map.Entry<String, Module> module : modules) {
                HudRenderUtil.renderText(context, module.getKey(), xPos - simpleCalcH(RENDERER.getStringWidth(module.getKey())), yPos);
                if (directionV() == TextDirectionV.BottomToTop)
                    yPos -= moduleOffset;
                else
                    yPos += moduleOffset;
            }
        }
    }

    public String getHudName(Module module)
    {
        return module.getDisplayName()
                + (module.getDisplayInfo() == null
                || module.isHidden() == Hidden.Info
                ? ""
                : Formatting.GRAY
                + actualBracket()[0] + TextColor.WHITE
                + module.getDisplayInfo()
                + TextColor.GRAY + actualBracket()[1]);
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ " " + HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[0] + HudRenderUtil.bracketsTextColor(), HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[1] + TextColor.WHITE };
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
        super("Modules", HudCategory.Text, 200, 200);
        ListenerPostKey listener = new ListenerPostKey(this);
        this.listeners.add(listener);
        listener.invoke(new KeyboardEvent.Post());

        if (moduleRender.getValue() == ModuleSorting.Length)
            modules.sort(Comparator.comparing(entry -> Managers.TEXT.getStringWidth(entry.getKey()) *  -1));
        else
            modules.sort(Map.Entry.comparingByKey());

        this.setData(new SimpleHudData(this, "If you want to show enabled modules."));
    }

    protected boolean isArrayMember(Module module) {
        return getArrayEntries().containsKey(module)
                || module instanceof PbModule
                && getArrayEntries().containsKey(((PbModule) module)
                .getModule());
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
