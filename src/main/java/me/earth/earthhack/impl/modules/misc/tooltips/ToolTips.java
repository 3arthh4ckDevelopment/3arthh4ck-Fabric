package me.earth.earthhack.impl.modules.misc.tooltips;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerItemsData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ToolTips extends Module {

    protected final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Hover));
    protected final Setting<Boolean> vanillaStyle =
            register(new BooleanSetting("VanillaStyle", false));
    protected final Setting<Color> boxColor =
            register(new ColorSetting("BoxColor", new Color(32, 32, 32, 100)));
    protected final Setting<Boolean> showColor =
            register(new BooleanSetting("ShowColor", true));
    protected final Setting<Boolean> maps =
            register(new BooleanSetting("Maps", true));

    protected final List<ShulkerItemsData> itemsDataList = new ArrayList<>();
    protected int scrollAmount;

    public ToolTips() {
        super("ToolTips", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerToolTip(this));
        this.listeners.add(new ListenerInventoryRender(this));
        this.listeners.add(new ListenerInventoryClick(this));
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    enum Mode {
        Hover,
        Right,
        Left
    }

}

