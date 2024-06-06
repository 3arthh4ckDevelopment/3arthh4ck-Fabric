package me.earth.earthhack.api.hud;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

public class HudPositionOffsetManager extends SubscriberImpl {
    private static final HudPositionOffsetManager INSTANCE = new HudPositionOffsetManager();

    private Screen screen = null;
    private float y = 0;
    private float endY = 2000;

    public HudPositionOffsetManager() {
        Bus.EVENT_BUS.register(new LambdaListener<>(GuiScreenEvent.class, e -> {
            if (e.getScreen() == null) {
                HudPositionOffsetManager.setEndY(MinecraftClient.getInstance().getWindow().getScaledHeight());
            }

            if (e.getScreen() instanceof ChatScreen && screen == null) {
                screen = e.getScreen();
                HudPositionOffsetManager.setEndY(HudPositionOffsetManager.getEndY() - 12);
            } else if (screen != null) {
                screen = null;
                HudPositionOffsetManager.setEndY(HudPositionOffsetManager.getEndY() + 12);
            }
        }));
    }

    public static float getY() {
        return INSTANCE.y;
    }

    public static void setY(float y) {
        INSTANCE.y = y;
    }

    public static float getEndY() {
        return INSTANCE.endY;
    }

    public static void setEndY(float endY) {
        INSTANCE.endY = endY;
    }
}
