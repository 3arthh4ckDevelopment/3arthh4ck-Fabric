package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Utility Interface for Styles, since after 1.12.2 they removed setClickEvent() and setHoverEvent().
 */
@Mixin(Style.class)
public interface IStyle {
    @Accessor("clickEvent")
    void setClickEvent(ClickEvent event);

    @Accessor("hoverEvent")
    void setHoverEvent(HoverEvent event);

}
