package me.earth.earthhack.impl.gui.chat;

import com.google.common.collect.Lists;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

/**
 * A Custom TextComponent.
 * <p></p>
 * <p>Important Note:
 * <p>The Parent Component should always be an AbstractComponent!
 * Using a {@link MutableText} for example and appending
 * AbstractComponents won't work properly. (TODO: fix)
 * For now you can use {@link AbstractTextComponent#EMPTY} for convenience.
 */
public abstract class AbstractTextComponent extends MutableText
{
    public static final AbstractTextComponent EMPTY = new AbstractTextComponent("")
    {
        @Override
        public TextContent getContent()
        {
            return Text.of(EMPTY.toString()).getContent();
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public String getUnformattedComponentText()
        {
            return "";
        }

        @Override
        public MutableText copy()
        {
            return EMPTY;
        }
    };

    private boolean wrap;

    public AbstractTextComponent(String initial)
    {
         super(Text.empty().append(initial).getContent(), Lists.newArrayList(), Style.EMPTY);
    }

    /**
     * Note that the wrapping on AbstractComponents is different,
     * (and a bit wonky) to preserve the integrity of all siblings
     * and components. So rather try not to wrap these.
     *
     * @param wrap returned by {@link AbstractTextComponent#isWrapping()}
     */
    public AbstractTextComponent setWrap(boolean wrap)
    {
        this.wrap = wrap;
        return this;
    }

    /**
     * @return <tt>false</tt> if this component can go
     *          outside the limits of chat.
     */
    public boolean isWrapping()
    {
        return this.wrap;
    }

    public abstract String getText();

    public abstract String getUnformattedComponentText();

    @Override
    public abstract MutableText copy();

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if (!(o instanceof AbstractTextComponent))
        {
            return false;
        }
        else
        {
            return this.getText().equals(((AbstractTextComponent) o).getText());
        }
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public String toString()
    {
        return "CustomComponent{text='"
                + this.getText()
                + '\''
                + ", siblings="
                + this.getSiblings()
                + ", style="
                + this.getStyle()
                + '}';
    }

}
