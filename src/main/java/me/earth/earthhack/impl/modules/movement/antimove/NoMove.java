package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.util.client.SimpleData;

public class NoMove extends Module
{
    protected final Setting<StaticMode> mode =
            register(new EnumSetting<>("Mode", StaticMode.Stop));
    protected final Setting<Float> height    =
            register(new NumberSetting<>("Height", 4.0f, -64.0f, 256.0f));
    protected final Setting<Boolean> timer    =
            register(new BooleanSetting("Timer", false));

    public NoMove()
    {
        super("Static", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUpdate(this));

        SimpleData data = new SimpleData(this,
                "Stops all Movement depending on the mode.");
        data.register(mode, """
                - Stop : Stops all movement while this module is enabled. Can be used to lag you back up when you fall.
                - NoVoid : stops all movement if there's void underneath you.
                - Roof : used to tp you up 120 blocks on certain servers.""");
        data.register(height, "At which height to stop movement when using Mode - NoVoid." +
                " Keep in mind that 1.12 servers have the void at Y level 0 and 1.17+ has it at Y level -64.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

}
