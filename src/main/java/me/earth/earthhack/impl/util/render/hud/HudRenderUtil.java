package me.earth.earthhack.impl.util.render.hud;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class HudRenderUtil implements Globals {
    private static final TextRenderer RENDERER = Managers.TEXT;
    private static final ModuleCache<HudEditor> HUD_EDITOR =
            Caches.getModule(HudEditor.class);
    private static final ModuleCache<FontMod> CUSTOM_FONT =
            Caches.getModule(FontMod.class);

    public static String[] brackets() {
        return HUD_EDITOR.get().getBrackets();
    }

    public static String getBracketColor() {
        if (!(HUD_EDITOR.get().bracketsColor.getValue() == TextColor.None))
            return HUD_EDITOR.get().bracketsColor.getValue().getColor();
        else
            return TextColor.GRAY;
    }

    public static String bracketsTextColor() {
        if (!(HUD_EDITOR.get().insideText.getValue() == TextColor.None))
            return HUD_EDITOR.get().insideText.getValue().getColor();
        else
            return TextColor.GRAY;
    }

    public static void renderText(DrawContext context, String text, float x, float y) {
        renderText(context, text, x, y, 1.0f);
    }

    public static void renderText(DrawContext context, String text, float x, float y, float scale) {
        String colorCode = HUD_EDITOR.get().colorMode.getValue().getColor();
        RENDERER.drawStringScaled(context, colorCode + text, x, y, textColor(y), true, scale);
    }

    public static void drawItemStack(DrawContext context, ItemStack stack, int x, int y, boolean amount)
    {
        context.drawItem(stack, x, y, 1);
        if (amount) {
            String count = String.valueOf(stack.getCount());
            if (CUSTOM_FONT.isEnabled()) {
                context.drawItem(stack, x, y);
                if (stack.getCount() > 1)
                    Managers.TEXT.drawString(context, count, x + 19 - 2 - Managers.TEXT.getStringWidth(count), y + 9, HUD_EDITOR.get().color.getValue().getRGB(), true);
            } else {
                context.drawItemInSlot(mc.textRenderer, stack, x, y);
            }
        }
    }

    private static int textColor(float y) {
        return HUD_EDITOR.get().colorMode.getValue() == HudRainbow.None
                ? HUD_EDITOR.get().color.getValue().getRGB()
                    : (HUD_EDITOR.get().colorMode.getValue() == HudRainbow.Static
                    ? (ColorUtil.staticRainbow((y + 1) * 0.89f, HUD_EDITOR.get().color.getValue()))
                : 0xffffffff);
    }
}
