package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.client.commands.Commands;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FontMod extends Module {

    public final Setting<String> fontName =
            register(new StringSetting("Font", "Verdana"));
    public final Setting<Float> fontSize =
            register(new NumberSetting<>("Size", 9.0f, 4.0f, 12.0f));
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
                if (!isFontAvailable(event.getValue())) {
                    ChatUtil.sendMessage("Font not found, loading fallback!", getName());
                } else {
                    ChatUtil.sendMessage("Font " + event.getValue() + " found and loaded!", getName());
                }
                TextRenderer.FONTS.reInit();
            }
        });

        this.setData(new FontData(this));
    }

    public byte[] getSelectedFont() {
        Optional<Path> fontPath = getOSFontPaths().stream()
                .map(path -> Paths.get(path, fontName.getValue().toLowerCase() + ".ttf"))
                .filter(Files::exists)
                .findFirst();

        if (!fontPath.isPresent()) {
            fontPath = getOSFontPaths().stream()
                    .map(path -> Paths.get(path, fontName.getValue() + ".TTF"))
                    .filter(Files::exists)
                    .findFirst();
        }

        try {
            if (fontPath.isPresent()) {
                ChatUtil.sendMessage("Using font: " + fontPath.get().toString(), getName());
                return Files.readAllBytes(fontPath.get());
            } else {
                ChatUtil.sendMessage("Font not found, loading fallback!", getName());
                return Earthhack.class.getClassLoader()
                        .getResourceAsStream("assets/earthhack/fallback-font-corbel.ttf")
                        .readAllBytes();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font", e);
        }
    }

    private List<String> getOSFontPaths() {
        List<String> paths = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            paths.add("C:\\Windows\\Fonts");
        } else if (os.contains("darwin")) {
            paths.add("/Library/Fonts/");
        } else if (os.contains("linux")) {
            paths.add(System.getProperty("user.home") + "/.local/share/fonts");
            paths.add("/usr/share/fonts");
            paths.add("/usr/share/fonts/TTF"); // Added TTF directory
            paths.add("/usr/local/share/fonts");
        }
        return paths;
    }

    private boolean isFontAvailable(String fontName) {
        String lowerCaseFontName = fontName.toLowerCase() + ".ttf";
        String upperCaseFontName = fontName + ".TTF";
        return getAllFonts().stream()
                .anyMatch(font -> font.equalsIgnoreCase(lowerCaseFontName) || font.equalsIgnoreCase(upperCaseFontName));
    }

    private List<String> getAllFonts() {
        List<String> fonts = new ArrayList<>();
        for (String path : getOSFontPaths()) {
            File directory = new File(path);
            if (directory.exists() && directory.isDirectory()) {
                fonts.addAll(getFontsInDirectory(directory));
            }
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
                } else if (file.getName().toLowerCase().endsWith(".ttf") || file.getName().endsWith(".TTF")) {
                    fonts.add(file.getName());
                }
            }
        }
        return fonts;
    }

    public void sendFonts() {
        MutableText component =
                Text.empty().append("Available Fonts: ");

        List<String> fonts = getAllFonts().stream()
                .map(x -> x.replace(".ttf", "").replace(".TTF", ""))
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());

        for (int i = 0; i < fonts.size(); i++) {
            String font = fonts.get(i);
            if (font != null) {
                int finalI = i;
                component.append(
                        new SuppliedComponent(() ->
                                (font.equalsIgnoreCase(fontName.getValue())
                                        ? TextColor.GREEN
                                        : TextColor.RED)
                                        + font
                                        + (finalI == fonts.size() - 1
                                        ? ""
                                        : ", "))
                                .setStyle(Style.EMPTY.withClickEvent(
                                        new SmartClickEvent
                                                (ClickEvent.Action.RUN_COMMAND) {
                                            @Override
                                            public String getValue() {
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
