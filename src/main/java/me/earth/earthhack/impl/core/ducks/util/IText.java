package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * A duck interface for {@link Text}.
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
    void earthhack$setFormattingHook(Supplier<String> hook);

    /**
     * @param hook overrides {@link Text#withoutStyle()}.
     */
    void earthhack$setUnFormattedHook(Supplier<String> hook);

    Text earthhack$copyNoSiblings();

}
