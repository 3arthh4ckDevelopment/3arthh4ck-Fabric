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
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Calendar;

public class Greeter extends HudElement {

    private final Setting<GreeterMode> greeterMode =
            register(new EnumSetting<>("Mode", GreeterMode.LongNew));
    protected final Setting<String> custom =
            register(new StringSetting("Custom", "Welcome to Future Beta >:D"));
    private String text;

    private void render(DrawContext context) {
        if (mc.player == null) return;

        String name = mc.player.getGameProfile().getName();
        String playerName = Caches.getModule(Media.class).returnIfPresent(m -> m.convert(name), name);

        if (greeterMode.getValue().equals(GreeterMode.Custom))
            text = custom.getValue().replace("%player%", playerName);
        else
            text = greeterMode.getValue().getMessage(playerName);
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
        //super("Greeter", HudCategory.Text,  (Render2DUtil.getScreenWidth() / 2.0f) - (Managers.TEXT.getStringWidth(text) / 2.0f), 2);
        super("Greeter", HudCategory.Text,  10, 2);
        this.setData(new SimpleHudData(this, "Greets you."));
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
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

    private enum GreeterMode {
        Time(getTimeOfDay() + "%player%"),
        LongNew("Welcome to " + Earthhack.NAME + " %player% :^)"),
        LongVer("Welcome to " + Earthhack.NAME + " " + Earthhack.VERSION + " %player% :^)"),
        LongOld("Welcome to Phobos.eu %player% :^)"),
        Weird("Welcome to phobro hack %player%"),
        Custom(""),
        Simple("Welcome %player%");

        private final String message;

        GreeterMode(String message) {
            this.message = message;
        }

        public String getMessage(String player) {
            return message.replace("%player%", player);
        }
    }

}
