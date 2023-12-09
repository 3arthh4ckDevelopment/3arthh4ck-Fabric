package me.earth.earthhack.impl.core.mixins.util;

/*
import java.util.function.Supplier;

@Mixin(Text.class)
public abstract class MixinTextComponentBase
        implements ITextComponentBase
{
    @Shadow public abstract MutableText copy();

    private Supplier<String> hookFormat;
    private Supplier<String> hookUnFormat;

    @Override
    public void setFormattingHook(Supplier<String> hook)
    {
        this.hookFormat = hook;
    }

    @Override
    public void setUnFormattedHook(Supplier<String> hook)
    {
        this.hookUnFormat = hook;
    }

    @Override
    public Text copyNoSiblings()
    {
        Text copy = this.copy();
        copy.getSiblings().clear();

        return copy;
    }

    @Inject(
        method = "getWithStyle(Lnet/minecraft/text/Style;)Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true)
    public void getFormattedTextHook(CallbackInfoReturnable<String> info)
    {
        if (hookFormat != null)
        {
            info.setReturnValue(hookFormat.get());
        }
    }



    @Inject(
        method = "withoutStyle()Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true)
    public void getUnformattedTextHook(CallbackInfoReturnable<String> info)
    {
        if (hookUnFormat != null)
        {
            info.setReturnValue(hookUnFormat.get());
        }
    }
}

 */
