package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.commands.gui.EarthhackButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen implements Globals
{
    private MixinTitleScreen() {
        super(Text.of("3arthh4ck"));
    }

    @Unique
    private EarthhackButton earthhackButton;

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 2,
                    shift = At.Shift.AFTER,
                    remap = false))
    public void buttonHook(CallbackInfo info)
    {
        TitleScreen _this = TitleScreen.class.cast(this);

        earthhackButton = (EarthhackButton) ButtonWidget.builder(Text.literal("3arthh4ck"),
                button -> mc.setScreen(new CommandGui(_this, 2500)))
                .tooltip(Tooltip.of(Text.literal("Opens the 3arthh4ck Command GUI.")))
                .dimensions(this.width / 2 + 2 + 98 + 4, this.height / 4 + 48 + 72 + 12, 20, 20)
                .build();

        this.addDrawableChild(earthhackButton);
    }

}
