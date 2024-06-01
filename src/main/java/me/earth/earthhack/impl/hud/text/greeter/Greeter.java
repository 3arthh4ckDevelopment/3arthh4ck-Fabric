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
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Calendar;

public class Greeter extends HudElement {

    private final Setting<Mode> greeterMode =
            register(new EnumSetting<>("Mode", Mode.LongNew));

    private String text = "";
    private static String customString = "";

    @Override
    protected void onRender(DrawContext context) {
        String name = RotationUtil.getRotationPlayer().getDisplayName().getString().trim();
        String playerName = Caches.getModule(Media.class).returnIfPresent(m -> m.convert(name), name);
        HudRenderUtil.renderText(context, text = greeterMode.getValue().getString(playerName), getX(), getY());
    }

    public static String getTimeOfDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        // this is ugly, ternary operators make it more ugly though so =)
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
        Setting<String> custom = register(new StringSetting("Custom", "Welcome to Future Beta >:D"));
        custom.addObserver(e -> {
           if (!e.getValue().isEmpty())
               customString = e.getValue();
        });
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text.trim());
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

    private enum Mode {
        Time {
            @Override
            public String getString(String playerName) {
                return getTimeOfDay() + playerName;
            }
        },
        LongNew {
            @Override
            public String getString(String playerName) {
                return "Welcome to " + Earthhack.NAME + " " + playerName + " :^)";
            }
        },
        LongVer {
            @Override
            public String getString(String playerName) {
                return "Welcome to " + Earthhack.NAME + " " + Earthhack.VERSION + " " + playerName + " :^)";
            }
        },
        LongOld {
            @Override
            public String getString(String playerName) {
                return "Welcome to Phobos.eu " + playerName + " :^)";
            }
        },
        Weird {
            @Override
            public String getString(String playerName) {
                return "Welcome to phobro hack " + playerName;
            }
        },
        Simple {
            @Override
            public String getString(String playerName) {
                return "Welcome " + playerName;
            }
        },
        Custom {
            @Override
            public String getString(String playerName) {
                return customString.replace("%player%", playerName);
            }
        };

        public abstract String getString(String playerName);
    }
}
