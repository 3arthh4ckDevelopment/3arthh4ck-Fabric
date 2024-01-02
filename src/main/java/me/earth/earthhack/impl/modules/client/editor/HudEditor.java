package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.gui.hud.HudEditorGui;
import me.earth.earthhack.impl.util.render.hud.HudRainbow;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

import java.awt.*;

public class HudEditor extends Module {

    protected final Setting<Boolean> show =
            register(new BooleanSetting("Show", true));
    public final Setting<HudRainbow> colorMode =
            register(new EnumSetting<>("Rainbow", HudRainbow.None));
    public final Setting<Color> color =
            register(new ColorSetting("Color", Color.WHITE));
    public final Setting<Boolean> shadow =
            register(new BooleanSetting("Text-Shadow", true));
    public final Setting<Boolean> testShadow =
            register(new BooleanSetting("OffsetShadow", true))
                    .setComplexity(Complexity.Expert);
    public final Setting<TextColor> bracketsColor =
            register(new EnumSetting<>("BracketsColor", TextColor.None));
    public final Setting<TextColor> insideText =
            register(new EnumSetting<>("BracketsTextColor", TextColor.White));
    public final Setting<String> brackets =
            register(new StringSetting("Brackets", "[:]"));

    public HudEditor() {
        super("HudEditor", Category.Client);
        Bus.EVENT_BUS.register(new ListenerRender(this));
        this.setData(new HudEditorData(this));

        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (!(mc.currentScreen instanceof ChatScreen)) {
                toggle();
                HudEditorGui hud = new HudEditorGui(Text.of("HUD"));
                hud.init();
                hud.onGuiOpened();
                mc.setScreen(hud);
            }
        }));

        /*
        brackets.addObserver(e -> {
            StringBuilder b = new StringBuilder();
            for (char c : brackets.getValue().toCharArray()) {
                 b.append(StringComponent.isAllowedCharacter(c));
            }
            String start, end;
            if (b.toString().contains(":")) {
                start = brackets.getValue().substring(0, brackets.getValue().indexOf(":"));
                if (start.isEmpty())
                    brackets.setValue(brackets.getInitial());

                end = brackets.getValue().substring(brackets.getValue().indexOf(":") + 1);
                if (end.isEmpty())
                    brackets.setValue(brackets.getInitial());
            } else {
                brackets.setValue(brackets.getInitial());
            }
        });

         */
    }

    public final String[] getBrackets() {
        return new String[]{
                brackets.getValue().substring(0, brackets.getValue().indexOf(":")),
                brackets.getValue().substring(brackets.getValue().indexOf(":") + 1)};
    }

    @Override
    public void onLoad() {
        this.disable();
    }

}
