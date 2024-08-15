package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.event.events.render.InventoryRenderEvent;
import me.earth.earthhack.impl.event.events.render.ToolTipEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HandledScreen.class)
public class MixinHandledScreen extends Screen {

    @Unique
    private static final ModuleCache<ToolTips> TOOL_TIPS =
            Caches.getModule(ToolTips.class);

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Inject(method = "drawMouseoverTooltip",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void drawMouseoverTooltipMixin(DrawContext context, int x, int y, CallbackInfo ci, ItemStack itemStack) {
        ToolTipEvent event = new ToolTipEvent(context, itemStack, x, y);
        Bus.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"), cancellable = true)
    private void mouseClickedHook(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryRenderEvent.InventoryClickEvent event = new InventoryRenderEvent.InventoryClickEvent(mouseX, mouseY);
        Bus.EVENT_BUS.post(event);
        cir.setReturnValue(true);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        TOOL_TIPS.get().setScrollAmount((int) (TOOL_TIPS.get().getScrollAmount() + verticalAmount * 10));
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

}
