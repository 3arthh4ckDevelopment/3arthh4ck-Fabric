package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.impl.util.render.image.EarthhackTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;

public class EarthhackButton {
    public static TextIconButtonWidget createEarthhackButton(int width,
                                                             ButtonWidget.PressAction onPress,
                                                             boolean hideText) {
        return TextIconButtonWidget.builder(Text.of("3arthh4ck"),
                onPress, hideText)
                .width(width)
                .texture(EarthhackTextures.SKIN, 16, 16)
                .build();
    }
}
