package me.earth.earthhack.impl.gui.chat.components;

import me.earth.earthhack.impl.core.ducks.util.IHoverable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SuppliedHoverableComponent extends SuppliedComponent
        implements IHoverable
{
    private final BooleanSupplier canBeHovered;

    public SuppliedHoverableComponent(Supplier<String> supplier,
                                      BooleanSupplier canBeHovered)
    {
        super(supplier);
        this.canBeHovered = canBeHovered;
    }

    @Override
    public boolean canBeHovered()
    {
        return canBeHovered.getAsBoolean();
    }

    @Override
    public MutableText copy()
    {
        SuppliedHoverableComponent copy =
                new SuppliedHoverableComponent(supplier, canBeHovered);

        copy.setStyle(this.getStyle());

        for (Text component : this.getSiblings())
        {
            copy.append(component.copy());
        }

        return copy;
    }

}
