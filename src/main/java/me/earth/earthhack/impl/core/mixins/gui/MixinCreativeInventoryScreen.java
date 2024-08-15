package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.InventoryRenderEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {

    @Unique
    private static final ModuleCache<ToolTips> TOOL_TIPS =
            Caches.getModule(ToolTips.class);

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Bus.EVENT_BUS.post(new InventoryRenderEvent(context));
    }

    @Inject(method = "mouseScrolled", at = @At(value = "RETURN"))
    private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            TOOL_TIPS.get().setScrollAmount(TOOL_TIPS.get().getScrollAmount() + (int) verticalAmount * 10);
        }
    }
}
