package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Simple Implementation of {@link AbstractTextComponent}.
 */
public class SimpleComponent extends AbstractTextComponent
{
    private final String text;

    public SimpleComponent(String initial)
    {
        super(initial);
        this.text = initial;
    }

    @Override
    public String getText()
    {
        return text;
    }

    @Override
    public String getUnformattedComponentText()
    {
        return text;
    }

    @Override
    public MutableText copy()
    {
        SimpleComponent copy = new SimpleComponent(this.text);
        copy.setStyle(this.getStyle());

        for (Text sibling : this.getSiblings())
        {
            copy.append(sibling.copy());
        }

        return copy;
    }

}
