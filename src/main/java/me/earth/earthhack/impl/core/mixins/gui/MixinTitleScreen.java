package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    private static final Identifier EARTHHACK_TEXTURES = new Identifier("earthhack:textures/gui/gui_textures.png");

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
        // noinspection ConstantConditions
        this.addDrawableChild(new TexturedButtonWidget(
                this.width / 2 + 2 + 98 + 4,
                this.height / 4 + 48 + 72 + 12 - (20 + 4 * 4),
                20, 20,
                0, 40, 20,
                EARTHHACK_TEXTURES,
                256, 256,
                button -> this.client.setScreen(new CommandScreen(new TitleScreen()))));
    }

}
