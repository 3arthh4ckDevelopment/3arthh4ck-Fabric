package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {

    @Unique
    private static final ModuleCache<ViewModel> VIEW_MODEL =
            Caches.getModule(ViewModel.class);


}
