package me.earth.earthhack.impl.modules.client.colors;

import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.ColorUtil;

import java.awt.*;

final class ListenerTick extends ModuleListener<Colors, TickEvent> {

    public ListenerTick(Colors module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        for (ColorSetting setting : Colors.cSettings) {
            if (setting.isSync()) {
                setting.setValueAlpha(Managers.COLOR.getColorSetting().getValue());
            } else if (setting.isRainbow()) { // sync setting is already false
                setting.setValueAlpha(setting.isStaticRainbow() ? new Color(ColorUtil.staticRainbow(0, setting.getStaticColor())) : ColorUtil.getRainbow((int) Math.max(setting.getRainbowSpeed() * 30.f, 30.f), 0, setting.getRainbowSaturation() / 100.f, setting.getRainbowBrightness() / 100.f));
            }
        }
    }

}
