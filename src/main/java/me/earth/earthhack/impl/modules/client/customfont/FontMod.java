package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.customfont.mode.FontStyle;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FontMod extends Module {

    public final Setting<String> fontName =
            register(new StringSetting("Font", "Verdana"));
    public final Setting<Float> fontSize =
            register(new NumberSetting<>("Size", 9.0f, 4.0f, 12.0f));
//    public final Setting<FontStyle> fontStyle = // TODO: This!
//            register(new EnumSetting<>("FontStyle", FontStyle.Plain));
    public final Setting<Float> shadowOffset =
            register(new NumberSetting<>("ShadowOffset", 0.6f, 0.2f, 1.0f));
    public final Setting<Boolean> blurShadow =
            register(new BooleanSetting("ShadowBlur", false));
    public final Setting<Boolean> showFonts =
            register(new BooleanSetting("Fonts", false));

    public FontMod() {
        super("CustomFont", Category.Client);

        showFonts.addObserver(event -> {
            if (event.getValue()) {
                event.setCancelled(true);
                sendFonts();
            }
        });

        fontName.addObserver(event -> {
            if (!event.getValue().isEmpty()) {
                if (!getAllFonts().contains(event.getValue().toLowerCase() + ".ttf")) {
                    ChatUtil.sendMessage("Font not found, loading fallback!", getName());
                }
                TextRenderer.FONTS.reInit();
            }
        });

        this.setData(new FontData(this));
    }

    public byte[] getSelectedFont() {
        try (FileInputStream fileInputStream = new FileInputStream(getOSFontPath() + FileSystems.getDefault().getSeparator() + fontName.getValue().toLowerCase() + ".ttf")) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            ChatUtil.sendMessage("Font not found, loading fallback!", getName());
            try {
                return Earthhack.class.getClassLoader().getResourceAsStream("assets/earthhack/fallback-font-corbel.ttf").readAllBytes();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private String getOSFontPath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return "C:\\Windows\\Fonts";
        }
        else if (os.contains("darwin")) {
            return "/Library/Fonts/";
        }
        else if (os.contains("linux")) {
            return System.getProperty("user.home") + "/.local/share/fonts";
        }
        return "none";
    }

    private List<String> getAllFonts() {
        String path = getOSFontPath();
        List<String> fonts = new ArrayList<>();
        if (!path.equals("none")) {
            File directory = new File(path);
            fonts = getFontsInDirectory(directory);
        }
        return fonts;
    }

    private List<String> getFontsInDirectory(File directory) {
        List<String> fonts = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fonts.addAll(getFontsInDirectory(file));
                } else if (file.getName().endsWith(".ttf")) {
                    fonts.add(file.getName());
                }
            }
        }
        return fonts;
    }

    public void sendFonts() {
        MutableText component =
                Text.empty().append("Available Fonts: ");
        
        List<String> fonts = getAllFonts().stream().map(x -> x.replace(".ttf", "")).toList();

        for (int i = 0; i < fonts.size(); i++) {
            String font = fonts.get(i);
            if (font != null) {
                int finalI = i;
                component.append(
                        new SuppliedComponent(() ->
                                (font.equals(fontName.getValue())
                                        ? TextColor.GREEN
                                        : TextColor.RED)
                                        + font
                                        + (finalI == fonts.size() - 1
                                        ? ""
                                        : ", "))
                                .setStyle(Style.EMPTY.withClickEvent(
                                        new SmartClickEvent
                                                (ClickEvent.Action.RUN_COMMAND)
                                        {
                                            @Override
                                            public String getValue()
                                            {
                                                return Commands.getPrefix()
                                                        + "CustomFont Font "
                                                        + "\"" + font + "\"";
                                            }
                                        })));
            }
        }

        Managers.CHAT.sendDeleteComponent(
                component, "Fonts", ChatIDs.MODULE);
    }
}
