package me.earth.earthhack.impl.hud.text.greeter;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Calendar;

public class Greeter extends HudElement {

    private final Setting<GreeterMode> greeterMode =
            register(new EnumSetting<>("Mode", GreeterMode.LongNew));
    private final Setting<String> custom =
            register(new StringSetting("Custom", "Welcome to Future Beta >:D"));

    public static String text = "";

    protected void onRender(DrawContext context) {
        String name = mc.player.getDisplayName().getString().trim();
        String playerName = Caches.getModule(Media.class).returnIfPresent(m -> m.convert(name), name);
        switch (greeterMode.getValue()) {
            case LongNew:
                text = "Welcome to " + Earthhack.NAME + " " + playerName + " :^)";
                break;
            case LongVer:
                text = "Welcome to " + Earthhack.NAME + " " + Earthhack.VERSION + " " + playerName + " :^)";
                break;
            case LongOld:
                text = "Welcome to Phobos.eu " + playerName + " :^)";
                break;
            case Time:
                text = getTimeOfDay() + playerName;
                break;
            case Weird:
                text = "Welcome to phobro hack " + playerName;
                break;
            case Custom:
                text = custom.getValue().replace("%player%", playerName);
                break;
            default:
                text = "Welcome " + playerName;
        }
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public static String getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay < 12){
            return "Good Morning ";
        } else if(timeOfDay < 16){
            return "Good Afternoon ";
        } else if(timeOfDay < 21){
            return "Good Evening ";
        } else {
            return "Good Night ";
        }
    }

    public Greeter() {
        super("Greeter", "Greets you.", HudCategory.Text, 200, 2);
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text.trim());
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

    private enum GreeterMode {
        Time,
        LongNew,
        LongVer,
        LongOld,
        Weird,
        Custom,
        Simple
    }
}
