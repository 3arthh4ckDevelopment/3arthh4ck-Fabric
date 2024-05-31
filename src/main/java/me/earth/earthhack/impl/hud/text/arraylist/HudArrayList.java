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
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.pingbypass.modules.PbModule;
import net.minecraft.client.gui.DrawContext;

import java.util.*;
import java.util.stream.Collectors;

public class HudArrayList extends DynamicHudElement {

    protected final Setting<ModuleSorting> moduleRender =
            register(new EnumSetting<>("ModuleSorting", ModuleSorting.Length));
    private final Setting<Integer> textOffset =
            register(new NumberSetting<>("Offset", 2, 0, 10));
    private final Setting<Boolean> animations =
            register(new BooleanSetting("Animations", false));

    protected final List<Map.Entry<String, Module>> modules = new ArrayList<>();
    private final Map<Module, HudArrayEntry> arrayEntries = new HashMap<>();
    private static final Map<Module, HudArrayEntry> removeEntries = new HashMap<>();

    private Map<Module, HudArrayEntry> getArrayEntries() {
        return arrayEntries;
    }

    protected static Map<Module, HudArrayEntry> getRemoveEntries() {
        return removeEntries;
    }

    protected void onRender(DrawContext context) {
        float xPos = getX() + simpleCalcX(getWidth());
        float yPos = (directionY() == TextDirectionY.BottomToTop ? getY() - Managers.TEXT.getStringHeight() * 4 : getY());
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

            Map<Module, HudArrayEntry> arrayEntriesSorted;
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
            if (directionY() == TextDirectionY.BottomToTop)
                yPos += moduleOffset * (modules.size() + 1);
            for (Map.Entry<String, Module> module : modules) {
                HudRenderUtil.renderText(context, module.getKey(), xPos - simpleCalcX(Managers.TEXT.getStringWidth(module.getKey())), yPos);
                yPos += moduleOffset * ((directionY() == TextDirectionY.BottomToTop) ? -1 : 1);
            }
        }
    }

    private boolean isArrayMember(Module module) {
        return getArrayEntries().containsKey(module)
                || module instanceof PbModule
                && getArrayEntries().containsKey(((PbModule) module)
                .getModule());
    }

    protected String getHudName(Module module) {
        return module.getDisplayName()
                + (module.getDisplayInfo() == null
                || module.isHidden() == Hidden.Info
                ? ""
                : surroundWithBrackets(module.getDisplayInfo()));
    }

    public HudArrayList() {
        super("ModuleList", "Displays enabled modules.", HudCategory.Text, 200, 200);
        this.listeners.add(new ListenerPostKey(this));
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
