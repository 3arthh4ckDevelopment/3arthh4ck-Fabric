package me.earth.earthhack.impl.core.mixins;

import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    /**
     * Sets the Window Title of Minecraft.
     * Applied through Mixin due to Display.setTitle being deprecated.
     * @param info the return value (String).
     */
    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> info)
    {
        info.setReturnValue(Earthhack.NAME + " - " + Earthhack.VERSION);
    }

}
