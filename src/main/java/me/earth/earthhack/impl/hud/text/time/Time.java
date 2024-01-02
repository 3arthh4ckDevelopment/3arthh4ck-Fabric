package me.earth.earthhack.impl.hud.text.time;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Time extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("Text", "Time"));
    private final Setting<String> timeFormat =
            register(new StringSetting("TimeFormat", "hh:mm:ss"));
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
    private String text = TextColor.GRAY + TextColor.RED + "Invalid";

    private void render() {
        LocalDateTime actualTime = LocalDateTime.now();
        try {
            text = name.getValue() + " " + TextColor.GRAY + formatter.format(actualTime);
        } catch (DateTimeException e) {
            Managers.CHAT.sendDeleteMessage(TextColor.RED + "Can not render time: " + e.getMessage(), this.getName(), ChatIDs.MODULE);
            text = name.getValue() + TextColor.GRAY + TextColor.RED + "Invalid";
        }

        HudRenderUtil.renderText(text, getX(), getY());
    }

    public Time() {
        super("Time", HudCategory.Text, 320, 70);
        this.setData(new SimpleHudData(this, "Displays the time."));

        timeFormat.addObserver(e -> {
            if (!e.isCancelled()) {
                try {
                    formatter = DateTimeFormatter.ofPattern(e.getValue());
                } catch (IllegalArgumentException iae) {
                    ChatUtil.sendMessageScheduled(TextColor.RED + "Invalid DateTimeFormat: " + TextColor.WHITE + e.getValue());
                    formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
                }
            }
        });
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
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
