package me.earth.earthhack.impl.modules.client.colors;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.managers.Managers;

import java.util.ArrayList;
import java.util.List;

public class Colors extends Module
{
    protected List<ColorSetting> cSettings = new ArrayList<>();
    public Colors()
    {
        super("Colors", Category.Client);
        register(Managers.COLOR.getColorSetting());
        register(Managers.COLOR.getRainbowSpeed());
        this.setData(new ColorsData(this));

        Managers.MODULES.getRegistered().forEach(module -> module.getSettings().stream().filter(setting -> setting instanceof ColorSetting).forEach(setting -> cSettings.add((ColorSetting) setting)));
        Managers.ELEMENTS.getRegistered().forEach(element -> element.getSettings().stream().filter(setting -> setting instanceof  ColorSetting).forEach(setting -> cSettings.add((ColorSetting) setting)));

        Bus.EVENT_BUS.register(new ListenerTick(this));
    }

}
