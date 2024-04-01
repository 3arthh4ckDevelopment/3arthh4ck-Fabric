package me.earth.earthhack.impl.managers.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.util.render.NVGRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TextRenderer implements Globals
{
    private final ModuleCache<FontMod> fontMod =
            Caches.getModule(FontMod.class);

    public static final NVGRenderer FONTS = new NVGRenderer();

    private void drawString(DrawContext context, String text, float x, float y, int color, boolean shadow, float scale) {
        if (text == null || text.isEmpty())
            return;
        if (fontMod.isEnabled()) {
            if (FONTS.isInitialized()) {
                FONTS.startDraw();
                FONTS.drawText(text, x, y, fontMod.get().fontSize.getValue() * scale, new Color(color), shadow);
                FONTS.endDraw();
            } else {
                FONTS.initialize();
            }
        } else {
            context.getMatrices().scale(scale, scale, scale);
            context.drawText(mc.textRenderer, text, (int) (x / scale), (int) (y / scale), color, shadow);
            context.getMatrices().scale(1 / scale, 1 / scale, 1 / scale);
        }
    }

    public void drawStringWithShadow(DrawContext context, String text, float x, float y, int color) {
        drawString(context, text, x, y, color, true, 1);
    }

    public void drawString(DrawContext context, String text, float x, float y, int color) {
        drawString(context, text, x, y, color, false, 1);
    }

    public void drawString(DrawContext context, String text, float x, float y, int color, boolean dropShadow) {
        drawString(context, text, x, y, color, dropShadow, 1);
    }

    public void drawStringScaled(DrawContext context, String text, float x, float y, int color, boolean dropShadow, float scale) {
        drawString(context, text, x, y, color, dropShadow, scale);
    }

    public int getStringWidth(String text) {
        if (usingCustomFont()) {
            return (int) FONTS.getWidth(text);
        } else {
            return mc.textRenderer.getWidth(text);
        }
    }

    public int getStringHeightI() {
        if (usingCustomFont()) {
            return (int) FONTS.getHeight();
        } else {
            return mc.textRenderer.fontHeight;
        }
    }

    public float getStringHeight() {
        if (usingCustomFont()) {
            return FONTS.getHeight();
        } else {
            return mc.textRenderer.fontHeight;
        }
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
        List<String> lines = new ArrayList<>();
        boolean hasChars = true;

        while (hasChars) {
            if (getStringWidth(str) > wrapWidth) {
                int i = 0;
                while (getStringWidth(str.substring(0, i)) <= wrapWidth) {
                    i++;
                    if (i >= str.length()) {
                        break;
                    }
                }
                String cut = str.substring(0, i);
                lines.add(cut);
                str = str.substring(i);
            } else {
                lines.add(str);
                hasChars = false;
            }
        }
        return lines;
    }

    public boolean usingCustomFont() {
        return fontMod.isEnabled() && FONTS.isInitialized();
    }
}
