package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.impl.core.ducks.util.IStyle;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(Style.class)
public abstract class MixinStyle implements IStyle
{
    @Unique
    private ClickEvent rightClickEvent;
    @Unique
    private ClickEvent middleClickEvent;
    @Unique
    private Supplier<String> leftSupplier;
    @Unique
    private Supplier<String> rightSupplier;
    @Unique
    private Supplier<String> middleSupplier;


    @Override
    public void setRightClickEvent(ClickEvent event)
    {
        this.rightClickEvent = event;
    }

    @Override
    public ClickEvent getRightClickEvent()
    {
        return rightClickEvent;
    }

    @Override
    public void setMiddleClickEvent(ClickEvent event)
    {
        this.middleClickEvent = event;
    }

    @Override
    public ClickEvent getMiddleClickEvent()
    {
        return middleClickEvent;
    }

    @Override
    public void setSuppliedInsertion(Supplier<String> insertion)
    {
        this.leftSupplier = insertion;
    }

    @Override
    public void setRightInsertion(Supplier<String> rightInsertion)
    {
        this.rightSupplier = rightInsertion;
    }

    @Override
    public void setMiddleInsertion(Supplier<String> middleInsertion)
    {
        this.middleSupplier = middleInsertion;
    }

    @Override
    public String getRightInsertion()
    {
        return rightSupplier == null ? null : rightSupplier.get();
    }

    @Override
    public String getMiddleInsertion()
    {
        return middleSupplier == null ? null : middleSupplier.get();
    }

    @Inject(
            method = "getInsertion",
            at = @At("HEAD"),
            cancellable = true)
    public void getInsertionHook(CallbackInfoReturnable<String> info)
    {
        if (leftSupplier != null)
        {
            info.setReturnValue(leftSupplier.get());
        }
    }

    private void copyDucks(IStyle style)
    {
        style.setMiddleInsertion(this.middleSupplier);
        style.setRightInsertion(this.rightSupplier);
        style.setSuppliedInsertion(this.leftSupplier);
        style.setMiddleClickEvent(this.middleClickEvent);
        style.setRightClickEvent(this.rightClickEvent);
    }
}
