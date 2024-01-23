package me.earth.earthhack.impl.core.mixins.util;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard implements Globals {

    @Inject(method = "onKey",
            at = @At(value = "HEAD"))
    public void onKeyHook(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        if (mc.currentScreen == null)
            Bus.EVENT_BUS.post(new KeyboardEvent((action != 0), key, (char) key));
    }

}
