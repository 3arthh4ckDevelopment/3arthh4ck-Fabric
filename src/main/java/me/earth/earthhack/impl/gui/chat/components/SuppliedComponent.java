package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.core.ducks.util.ITextComponentBase;
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
     * {@link ITextComponentBase#setFormattingHook(Supplier)} and
     * {@link ITextComponentBase#setUnFormattedHook(Supplier)}.
     * If you don't want that, use those methods to set them to null.
     *
     * @param supplier the supplier supplying what is returned by
     *                 {@link SuppliedComponent#get()}.
     */
    public SuppliedComponent(Supplier<String> supplier)
    {
        super(supplier.get());
        this.supplier = supplier;

        ((ITextComponentBase) this)
                .setFormattingHook(new SimpleTextFormatHook(this));
        ((ITextComponentBase) this)
                .setUnFormattedHook(new SimpleTextFormatHook(this));
    }

    @Override
    public TextContent getContent()
    {
        return (TextContent) Text.empty().append(supplier.get());
    }

    @Override
    public String get() {
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
