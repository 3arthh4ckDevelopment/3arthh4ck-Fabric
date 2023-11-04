package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * A duck interface for {@link net.minecraft.text.Text}.
 *
 * Allows you to "override" the final methods
 * {@link Text#getFormattedText()} and
 * {@link Text#get ()}
 */
public interface ITextComponentBase
{
    /**
     * @param hook overrides {@link TextComponentBase#getFormattedText()}.
     */
    void setFormattingHook(Supplier<String> hook);

    /**
     * @param hook overrides {@link TextComponentBase#getUnformattedText()}.
     */
    void setUnFormattedHook(Supplier<String> hook);

    Text copyNoSiblings();



}
