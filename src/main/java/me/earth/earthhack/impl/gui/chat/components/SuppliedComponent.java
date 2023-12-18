package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.core.ducks.util.IText;
import me.earth.earthhack.impl.core.util.SimpleTextFormatHook;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.function.Supplier;

/**
 * An AbstractComponent that returns the
 * Text Supplied by the given {@link Supplier}.
 */
public class SuppliedComponent extends AbstractTextComponent
{
    /** The Supplier for this components text. */
    protected final Supplier<String> supplier;

    /**
     * Sets TextFormatting to a {@link SimpleTextFormatHook} with
     * {@link IText#setFormattingHook(Supplier)} and
     * {@link IText#setUnFormattedHook(Supplier)}.
     * If you don't want that, use those methods to set them to null.
     *
     * @param supplier the supplier supplying what is returned by
     *                 {@link SuppliedComponent#getText()}.
     */
    public SuppliedComponent(Supplier<String> supplier)
    {
        super(supplier.get());
        this.supplier = supplier;

        ((IText) this)
                .setFormattingHook(new SimpleTextFormatHook(this));
        ((IText) this)
                .setUnFormattedHook(new SimpleTextFormatHook(this));
    }

    @Override
    public TextContent getContent()
    {
        return (TextContent) Text.empty().append(supplier.get());
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getUnformattedComponentText()
    {
        return supplier.get();
    }

    @Override
    public MutableText copy()
    {
        SuppliedComponent copy = new SuppliedComponent(supplier);
        copy.setStyle(this.getStyle());
        for (Text component : this.getSiblings())
        {
            copy.append(component.copy());
        }

        return copy;
    }

}
