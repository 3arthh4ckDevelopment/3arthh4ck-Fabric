package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
    @Inject(method = "render", at = @At("RETURN"))
    public void renderHook(DrawContext context, float tickDelta, CallbackInfo ci)
    {
        Bus.EVENT_BUS.post(new Render2DEvent(context, tickDelta));
    }
}
