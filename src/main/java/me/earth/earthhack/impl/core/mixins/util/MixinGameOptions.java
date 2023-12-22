package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions
{
    @Final @Shadow private SimpleOption<Integer> fov;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initHook(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        this.fov.callbacks = new SimpleOption.ValidatingIntSliderCallbacks(30, 180);
    }
}
