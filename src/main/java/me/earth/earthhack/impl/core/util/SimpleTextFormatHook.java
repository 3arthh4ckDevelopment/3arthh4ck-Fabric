package me.earth.earthhack.impl.core.util;

import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.text.Text;

import java.util.function.Supplier;

/**
 * An implementation of {@link Supplier <String>} for
 * {@link ITextComponentBase},
 * that formats the given TextComponent with nothing
 * but the UnformattedComponentTexts of the component.
 */
public class SimpleTextFormatHook implements Supplier<String>
{
    private final AbstractTextComponent base;

    /**
     * @param base the base to format.
     */
    public SimpleTextFormatHook(AbstractTextComponent base)
    {
        this.base = base;
    }

    @Override
    public String get()
    {
        StringBuilder sb = new StringBuilder();

        for (Text component : base)
        {
            sb.append(component.getContent().toString());
        }

        return sb.toString();
    }

}
