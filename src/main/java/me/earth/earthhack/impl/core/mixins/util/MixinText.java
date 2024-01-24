package me.earth.earthhack.impl.core.mixins.util;

import com.mojang.brigadier.Message;
import me.earth.earthhack.impl.core.ducks.util.IText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(MutableText.class)
public abstract class MixinText
        implements IText, Message
{
    // @Shadow public abstract MutableText copy();
    @Unique private Supplier<String> hookFormat;
    @Unique private Supplier<String> hookUnFormat;

    @Shadow public abstract MutableText formatted(Formatting... formatting);

    @Override
    public void earthhack$setFormattingHook(Supplier<String> hook)
    {
        this.hookFormat = hook;
    }

    @Override
    public void earthhack$setUnFormattedHook(Supplier<String> hook)
    {
        this.hookUnFormat = hook;
    }

    @Override
    public Text earthhack$copyNoSiblings()
    {
        Text copy = this.formatted();
        copy.getSiblings().clear();
        return copy;
    }

    @Inject(
        method = "styled(Ljava/util/function/UnaryOperator;)Lnet/minecraft/text/MutableText;",
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
        method = "asOrderedText()Lnet/minecraft/text/OrderedText;",
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
