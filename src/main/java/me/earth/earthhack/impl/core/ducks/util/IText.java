package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * A duck interface for {@link net.minecraft.text.Text}.
 *
 * Allows you to "override" the final methods
 * {@link Text#getWithStyle(Style)} and
 * {@link Text#withoutStyle()}
 */
public interface IText
{
    /**
     * @param hook overrides {@link Text#getWithStyle(Style)}.
     */
    void setFormattingHook(Supplier<String> hook);

    /**
     * @param hook overrides {@link Text#withoutStyle()}.
     */
    void setUnFormattedHook(Supplier<String> hook);

    Text copyNoSiblings();

}
