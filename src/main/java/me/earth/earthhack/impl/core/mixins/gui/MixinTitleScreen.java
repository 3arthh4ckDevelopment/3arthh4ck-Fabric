package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.util.render.image.EarthhackTextures;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen implements Globals
{
    private MixinTitleScreen() {
        super(Text.of("3arthh4ck"));
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild" +
                            "(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 2,
                    shift = At.Shift.AFTER))
    public void buttonHook(CallbackInfo info)
    {
        //noinspection ConstantConditions
        this.addDrawableChild(new TexturedButtonWidget(
                this.width / 2 + 2 + 98 + 4,
                this.height / 4 + 48 + 72 + 12 - (20 + 4 * 4),
                20, 20,
                new ButtonTextures(EarthhackTextures.TITLE_TEXTURES[0], EarthhackTextures.TITLE_TEXTURES[1]),
                action -> this.client.setScreen(new CommandGui(new TitleScreen()))));
    }

}
