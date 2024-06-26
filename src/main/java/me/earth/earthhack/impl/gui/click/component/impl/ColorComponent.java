package me.earth.earthhack.impl.gui.click.component.impl;

import me.earth.earthhack.api.setting.event.SettingResult;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.gui.click.component.SettingComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class ColorComponent extends SettingComponent<Color, ColorSetting> {
    private final ColorSetting colorSetting;
    private boolean colorExtended, colorSelectorDragging, alphaSelectorDragging, hueSelectorDragging;
    private float hue, saturation, brightness, alpha;
    private boolean slidingSpeed, slidingSaturation, slidingBrightness;

    public ColorComponent(ColorSetting colorSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(colorSetting.getName(), posX, posY, offsetX, offsetY, width, height, colorSetting);
        this.colorSetting = colorSetting;
        float[] hsb = Color.RGBtoHSB(getColorSetting().getRed(), getColorSetting().getGreen(), getColorSetting().getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = getColorSetting().getAlpha() / 255.f;
    }


    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + 7 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);
        if (getClickGui().get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + getWidth() - 20, getFinishedY() + 4, getFinishedX() + getWidth() - 5, getFinishedY() + 11, 0.5f, getColorSetting().getRGB(), 0xff000000);
        else
            Render2DUtil.drawBorderedRect(context.getMatrices(), getFinishedX() + getWidth() - 20, getFinishedY() + 4.5f, getFinishedX() + getWidth() - 5.5f, getFinishedY() + 11, 0.5f, getColorSetting().getRGB(), 0xff000000);

        setHeight(isColorExtended() ? ((getColorSetting() == Managers.COLOR.getColorSetting() ? 134 : 154) + (getColorSetting().isRainbow() ? 56 : 0)) : 14);
        if (isColorExtended()) {
            final float expandedX = getFinishedX() + 1;
            final float expandedY = getFinishedY() + 14;

            final float colorPickerLeft = expandedX + 6;
            final float colorPickerTop = expandedY + 1;
            final float colorPickerRight = colorPickerLeft + (getWidth() - 20);
            final float colorPickerBottom = colorPickerTop + (getHeight() - (((getColorSetting() == Managers.COLOR.getColorSetting() ? 52 : 68) + (getColorSetting().isRainbow() ? 56 : 0))));

            final int selectorWhiteOverlayColor = new Color(0xFF, 0xFF, 0xFF, 180).getRGB();

            int colorMouseX = (int) MathUtil.clamp(mouseX, colorPickerLeft, colorPickerRight);
            int colorMouseY = (int) MathUtil.clamp(mouseY, colorPickerTop, colorPickerBottom);

            // Color picker

            Render2DUtil.drawRect(context.getMatrices(), colorPickerLeft - 0.5F, colorPickerTop - 0.5F,
                    colorPickerRight + 0.5F, colorPickerBottom + 0.5F, 0xFF000000);

            drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);

            float colorSelectorX = saturation * (colorPickerRight - colorPickerLeft);
            float colorSelectorY = (1 - brightness) * (colorPickerBottom - colorPickerTop);

            if (colorSelectorDragging) {
                float wWidth = colorPickerRight - colorPickerLeft;
                float xDif = colorMouseX - colorPickerLeft;
                this.saturation = xDif / wWidth;
                colorSelectorX = xDif;

                float hHeight = colorPickerBottom - colorPickerTop;
                float yDif = colorMouseY - colorPickerTop;
                this.brightness = 1 - (yDif / hHeight);
                colorSelectorY = yDif;

                updateColor(Color.HSBtoRGB(hue, saturation, brightness));
            }

            // Color selector

            final float csLeft = colorPickerLeft + colorSelectorX - 0.5f;
            final float csTop = colorPickerTop + colorSelectorY - 0.5f;
            final float csRight = colorPickerLeft + colorSelectorX + 0.5f;
            final float csBottom = colorPickerTop + colorSelectorY + 0.5f;


            Render2DUtil.drawRect(context.getMatrices(), csLeft - 1, csTop - 1, csLeft, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), csRight, csTop - 1, csRight + 1, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), csLeft, csTop - 1, csRight, csTop,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), csLeft, csBottom, csRight, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), csLeft, csTop, csRight, csBottom, selectorWhiteOverlayColor);


            // Hue bar

            final float hueSliderLeft = colorPickerRight + 2;
            final float hueSliderRight = hueSliderLeft + 4;

            int hueMouseY = (int) MathUtil.clamp(mouseY, colorPickerTop, colorPickerBottom);

            final float hueSliderYDif = colorPickerBottom - colorPickerTop;

            float hueSelectorY = (1 - this.hue) * hueSliderYDif;

            if (hueSelectorDragging) {
                float yDif = hueMouseY - colorPickerTop;
                this.hue = 1 - (yDif / hueSliderYDif);
                hueSelectorY = yDif;

                updateColor(Color.HSBtoRGB(hue, saturation, brightness));
            }

            Render2DUtil.drawRect(context.getMatrices(), hueSliderLeft - 0.5F, colorPickerTop - 0.5F, hueSliderRight + 0.5F, colorPickerBottom + 0.5F,
                    0xFF000000);

            final float inc = 0.2F;
            final float times = 1 / inc;
            final float sHeight = colorPickerBottom - colorPickerTop;
            final float size = sHeight / times;
            float sY = colorPickerTop;

            // Draw colored hue bar
            for (int i = 0; i < times; i++) {
                boolean last = i == times - 1;
                Render2DUtil.drawGradientRect(context.getMatrices(), hueSliderLeft, sY, hueSliderRight,
                        sY + size, false,
                        Color.HSBtoRGB(1 - inc * i, 1.0F, 1.0F),
                        Color.HSBtoRGB(1 - inc * (i + 1), 1.0F, 1.0F));
                if (!last)
                    sY += size;
            }

            // Hue Selector

            final float hsTop = colorPickerTop + hueSelectorY - 0.5f;
            final float hsBottom = colorPickerTop + hueSelectorY + 0.5f;

            Render2DUtil.drawRect(context.getMatrices(), hueSliderLeft - 1, hsTop - 1, hueSliderLeft, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), hueSliderRight, hsTop - 1, hueSliderRight + 1, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), hueSliderLeft, hsTop - 1, hueSliderRight, hsTop,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), hueSliderLeft, hsBottom, hueSliderRight, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), hueSliderLeft, hsTop, hueSliderRight, hsBottom, selectorWhiteOverlayColor);


            // Alpha bar

            final float alphaSliderTop = colorPickerBottom + 2;
            final float alphaSliderBottom = alphaSliderTop + 4;

            int color = Color.HSBtoRGB(hue, saturation, brightness);

            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;

            final float hsHeight = colorPickerRight - colorPickerLeft;

            float alphaSelectorX = alpha * hsHeight;

            if (alphaSelectorDragging) {
                float xDif = colorMouseX - colorPickerLeft;
                this.alpha = xDif / hsHeight;
                alphaSelectorX = xDif;

                updateColor(new Color(r, g, b, (int) (alpha * 255)).getRGB());
            }

            Render2DUtil.drawRect(context.getMatrices(), colorPickerLeft - 0.5F, alphaSliderTop - 0.5F, colorPickerRight + 0.5F, alphaSliderBottom + 0.5F, 0xFF000000);

            Render2DUtil.drawCheckeredBackground(context.getMatrices(), colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);

            Render2DUtil.drawGradientRect(context.getMatrices(), colorPickerLeft, alphaSliderTop, colorPickerRight,
                    alphaSliderBottom, true,
                    new Color(r, g, b, 0).getRGB(),
                    new Color(r, g, b, 255).getRGB());

            // Alpha selector

            final float asLeft = colorPickerLeft + alphaSelectorX - 0.5f;
            final float asRight = colorPickerLeft + alphaSelectorX + 0.5f;


            Render2DUtil.drawRect(context.getMatrices(), asLeft - 1,
                    alphaSliderTop,
                    asRight + 1,
                    alphaSliderBottom,
                    0xFF000000);

            Render2DUtil.drawRect(context.getMatrices(), asLeft,
                    alphaSliderTop,
                    asRight,
                    alphaSliderBottom,
                    selectorWhiteOverlayColor);


            // Buttons

            Render2DUtil.drawGradientRect(context.getMatrices(), colorPickerLeft, alphaSliderBottom + 2, colorPickerLeft + ((getWidth() - 16) / 2), alphaSliderBottom + 14, false, getClickGui().get().getModulesColor().getRGB(), getClickGui().get().getModulesColor().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(context.getMatrices(), colorPickerLeft, alphaSliderBottom + 2, colorPickerLeft + ((getWidth() - 16) / 2), alphaSliderBottom + 14, 0.5f, 0, 0xff000000);
            drawStringWithShadow("Copy", colorPickerLeft + ((getWidth() - 16) / 2) / 2 - (Managers.TEXT.getStringWidth("Copy") >> 1), alphaSliderBottom + 8 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);

            Render2DUtil.drawGradientRect(context.getMatrices(), hueSliderRight - ((getWidth() - 16) / 2), alphaSliderBottom + 2, hueSliderRight, alphaSliderBottom + 14, false, getClickGui().get().getModulesColor().getRGB(), getClickGui().get().getModulesColor().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(context.getMatrices(), hueSliderRight - ((getWidth() - 16) / 2), alphaSliderBottom + 2, hueSliderRight, alphaSliderBottom + 14, 0.5f, 0, 0xff000000);
            drawStringWithShadow("Paste", hueSliderRight - ((getWidth() - 16) / 4) - (Managers.TEXT.getStringWidth("Paste") >> 1), alphaSliderBottom + 8 - (Managers.TEXT.getStringHeightI() >> 1), 0xFFFFFFFF);

            if (getColorSetting() != Managers.COLOR.getColorSetting()) {

                final boolean hoveredSync = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + 16, 12, 12);
                drawStringWithShadow("Sync", colorPickerLeft, alphaSliderBottom + 17, getColorSetting().isSync() ? 0xFFFFFFFF : 0xFFAAAAAA);
                Render2DUtil.drawBorderedRect(context.getMatrices(), hueSliderRight - 12, alphaSliderBottom + 16, hueSliderRight, alphaSliderBottom + 28, 0.5f, getColorSetting().isSync() ? (hoveredSync ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB()) : (hoveredSync ? 0x66333333 : 0), 0xff000000);
                if (getColorSetting().isSync())
                    Render2DUtil.drawCheckMark(context.getMatrices(), hueSliderRight - 6, alphaSliderBottom + 16, 10, 0xFFFFFFFF);
            }

            final boolean hoveredRainbow = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 12, 12);
            drawStringWithShadow("Rainbow", colorPickerLeft, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31), getColorSetting().isRainbow() ? 0xFFFFFFFF : 0xFFAAAAAA);
            Render2DUtil.drawBorderedRect(context.getMatrices(), hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), hueSliderRight, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 28 : 42), 0.5f, getColorSetting().isRainbow() ? (hoveredRainbow ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB()) : (hoveredRainbow ? 0x66333333 : 0), 0xff000000);
            if (getColorSetting().isRainbow()) {
                Render2DUtil.drawCheckMark(context.getMatrices(), hueSliderRight - 6, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 10, 0xFFFFFFFF);
                final float smallWidth = hueSliderRight - colorPickerLeft;
                final float lengthSpeed = MathHelper.floor((getColorSetting().getRainbowSpeed() / 200.f) * smallWidth);
                final float lengthSaturation = MathHelper.floor((getColorSetting().getRainbowSaturation() / 100.f) * smallWidth);
                final float lengthBrightness = MathHelper.floor((getColorSetting().getRainbowBrightness() / 100.f) * smallWidth);
                final float offset = alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31);

                final boolean hoveredStatic = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14, 12, 12);
                drawStringWithShadow("Static", colorPickerLeft, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31) + 14, getColorSetting().isStaticRainbow() ? 0xFFFFFFFF : 0xFFAAAAAA);
                Render2DUtil.drawBorderedRect(context.getMatrices(), hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14, hueSliderRight, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 28 : 42) + 14, 0.5f, getColorSetting().isStaticRainbow() ? (hoveredStatic ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB()) : (hoveredStatic ? 0x66333333 : 0), 0xff000000);
                if (getColorSetting().isStaticRainbow())
                    Render2DUtil.drawCheckMark(context.getMatrices(), hueSliderRight - 6, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14, 10, 0xFFFFFFFF);
                final boolean hoveredSpeed = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 28, smallWidth, 12.f);
                final boolean hoveredSaturation = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 42, smallWidth, 12.f);
                final boolean hoveredBrightness = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 56, smallWidth, 12.f);
                drawStringWithShadow("Speed: " + Formatting.GRAY + getColorSetting().getRainbowSpeed(), colorPickerLeft, offset + 28, 0xFFFFFFFF);
                drawStringWithShadow("Saturation: " + Formatting.GRAY + getColorSetting().getRainbowSaturation(), colorPickerLeft, offset + 42, 0xFFFFFFFF);
                drawStringWithShadow("Brightness: " + Formatting.GRAY + getColorSetting().getRainbowBrightness(), colorPickerLeft, offset + 56, 0xFFFFFFFF);
                Render2DUtil.drawBorderedRect(context.getMatrices(), colorPickerLeft, offset + 36.5f, colorPickerLeft + lengthSpeed, offset + 38.5f, 0.5f, hoveredSpeed ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB(), 0xff000000);
                if (slidingSpeed) {
                    float speedValue = ((mouseX - colorPickerLeft) * (200.f) / smallWidth);
                    getColorSetting().setRainbowSpeed(MathUtil.round(speedValue, 2, 0.f, 200.f));
                }
                Render2DUtil.drawBorderedRect(context.getMatrices(), colorPickerLeft, offset + 50.5f, colorPickerLeft + lengthSaturation, offset + 52.5f, 0.5f, hoveredSaturation ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB(), 0xff000000);
                if (slidingSaturation) {
                    float saturationValue = ((mouseX - colorPickerLeft) * (100.f) / smallWidth);
                    getColorSetting().setRainbowSaturation(MathUtil.round(saturationValue, 2, 0.f, 100.f));
                }
                Render2DUtil.drawBorderedRect(context.getMatrices(), colorPickerLeft, offset + 64.5f, colorPickerLeft + lengthBrightness, offset + 66.5f, 0.5f, hoveredBrightness ? getClickGui().get().getModulesColor().brighter().getRGB() : getClickGui().get().getModulesColor().getRGB(), 0xff000000);
                if (slidingBrightness) {
                    float brightnessValue = ((mouseX - colorPickerLeft) * (100.f) / smallWidth);
                    getColorSetting().setRainbowBrightness(MathUtil.round(brightnessValue, 2, 0.f, 100.f));
                }
            }
        }

        if (getColorSetting().isSync() || getColorSetting().isRainbow()) {
            float[] hsb = Color.RGBtoHSB(getColorSetting().getRed(), getColorSetting().getGreen(), getColorSetting().getBlue(), null);
            if (hue != hsb[0] || saturation != hsb[1] || brightness != hsb[2] || alpha != getColorSetting().getAlpha() / 255.f) {
                hue = hsb[0];
                saturation = hsb[1];
                brightness = hsb[2];
                alpha = getColorSetting().getAlpha() / 255.f;
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            final boolean hovered = Render2DUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 20, getFinishedY() + 4, 15, 7);
            if (isColorExtended()) {
                final float expandedX = getFinishedX() + 1;
                final float expandedY = getFinishedY() + 14;

                final float colorPickerLeft = expandedX + 6;
                final float colorPickerTop = expandedY + 1;
                final float colorPickerRight = colorPickerLeft + (getWidth() - 20);
                final float colorPickerBottom = colorPickerTop + (getHeight() - (((getColorSetting() == Managers.COLOR.getColorSetting() ? 52 : 68) + (getColorSetting().isRainbow() ? 56 : 0))));

                final float alphaSliderTop = colorPickerBottom + 2;
                final float alphaSliderBottom = alphaSliderTop + 4;

                final float hueSliderLeft = colorPickerRight + 2;
                final float hueSliderRight = hueSliderLeft + 4;

                final boolean hoveredCopy = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, alphaSliderBottom + 2, ((getWidth() - 16) / 2), 12);
                final boolean hoveredPaste = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - ((getWidth() - 16) / 2), alphaSliderBottom + 2, ((getWidth() - 16) / 2), 12);
                final boolean hoveredSync = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + 16, 12, 12);
                final boolean hoveredRainbow = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30), 12, 12);
                final float smallWidth = hueSliderRight - colorPickerLeft;
                final float offset = alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 17 : 31);
                final boolean hoveredStatic = Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + (getColorSetting() == Managers.COLOR.getColorSetting() ? 16 : 30) + 14, 12, 12);
                final boolean hoveredSpeed = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 28, smallWidth, 12.f);
                final boolean hoveredSaturation = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 42, smallWidth, 12.f);
                final boolean hoveredBrightness = Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, offset + 56, smallWidth, 12.f);

                if (hoveredRainbow) {
                    getColorSetting().setRainbow(!getColorSetting().isRainbow());
                }

                if (getColorSetting() != Managers.COLOR.getColorSetting() && hoveredSync) {
                    getColorSetting().setSync(!getColorSetting().isSync());
                }

                if (hoveredCopy) {
                    mc.keyboard.setClipboard(TextUtil.get32BitString(getColorSetting().getRGB()));
                    ChatUtil.sendMessage(TextColor.GREEN + "Color Copied: " + TextUtil.get32BitString(getColorSetting().getRGB()) + "!", null);
                }

                if (hoveredPaste) {
                    if (getClipBoard() != "") {
                        if (getColorSetting().fromString(getClipBoard()) == SettingResult.SUCCESSFUL) {
                            float[] hsb = Color.RGBtoHSB(getColorSetting().getRed(), getColorSetting().getGreen(), getColorSetting().getBlue(), null);
                            hue = hsb[0];
                            saturation = hsb[1];
                            brightness = hsb[2];
                            alpha = getColorSetting().getAlpha() / 255.f;
                            ChatUtil.sendMessage(TextColor.GREEN + "Color Pasted: " + getClipBoard() + "!", null);
                        } else {
                            ChatUtil.sendMessage(TextColor.RED + "Invalid Color!", null);
                        }
                    }
                }

                if (!getColorSetting().isSync()) {
                    if (!(getColorSetting().isRainbow() && !getColorSetting().isStaticRainbow())) {
                        if (!hoveredRainbow && !hoveredSync) {
                            if (Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, colorPickerTop - (((getColorSetting() == Managers.COLOR.getColorSetting()) ? 16 : 32) + (getColorSetting().isRainbow() ? 56 : 0)), (getWidth() - 20), (getHeight() - 36)))
                                colorSelectorDragging = true;

                            if (Render2DUtil.mouseWithinBounds(mouseX, mouseY, hueSliderLeft, colorPickerTop - (((getColorSetting() == Managers.COLOR.getColorSetting()) ? 16 : 32) + (getColorSetting().isRainbow() ? 56 : 0)), 4, (getHeight() - 36)))
                                hueSelectorDragging = true;
                        }
                    }
                }
                if (!hoveredRainbow && !hoveredSync && Render2DUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, alphaSliderTop, (getWidth() - 20), 4))
                    alphaSelectorDragging = true;

                if (getColorSetting().isRainbow()) {
                    if (hoveredStatic)
                        getColorSetting().setStaticRainbow(!getColorSetting().isStaticRainbow());
                    if (hoveredSpeed)
                        setSlidingSpeed(true);
                    if (hoveredSaturation)
                        setSlidingSaturation(true);
                    if (hoveredBrightness)
                        setSlidingBrightness(true);
                }
            }


            if (hovered)
                setColorExtended(!isColorExtended());
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (colorSelectorDragging)
                colorSelectorDragging = false;
            if (alphaSelectorDragging)
                alphaSelectorDragging = false;
            if (hueSelectorDragging)
                hueSelectorDragging = false;
            if (slidingSpeed)
                slidingSpeed = false;
            if (slidingSaturation)
                slidingSaturation = false;
            if (slidingBrightness)
                slidingBrightness = false;
        }
    }

    private String getClipBoard() {
        return mc.keyboard.getClipboard();
    }

    private void updateColor(int hex) {
        getColorSetting().setValue(new Color(
                hex >> 16 & 0xFF,
                hex >> 8 & 0xFF,
                hex & 0xFF,
                (int) (alpha * 255)));
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        final int hueBasedColor = Color.HSBtoRGB(hue, 1.0F, 1.0F);

        Render2DUtil.drawGradientRect(context.getMatrices(), left, top, right, bottom, true, 0xFFFFFFFF, hueBasedColor);

        Render2DUtil.drawGradientRect(context.getMatrices(), left, top, right, bottom, false, 0, 0xFF000000);
    }

    public ColorSetting getColorSetting() {
        return colorSetting;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public boolean isColorExtended() {
        return colorExtended;
    }

    public void setColorExtended(boolean colorExtended) {
        this.colorExtended = colorExtended;
    }

    public void setSlidingSpeed(boolean slidingSpeed) {
        this.slidingSpeed = slidingSpeed;
    }

    public void setSlidingSaturation(boolean slidingSaturation) {
        this.slidingSaturation = slidingSaturation;
    }

    public void setSlidingBrightness(boolean slidingBrightness) {
        this.slidingBrightness = slidingBrightness;
    }

}
