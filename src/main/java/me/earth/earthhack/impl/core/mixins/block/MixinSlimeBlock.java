package me.earth.earthhack.impl.core.mixins.block;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import net.minecraft.block.SlimeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeBlock.class)
public class MixinSlimeBlock
{
    @Unique
    private static final ModuleCache<NoSlowDown>
            NO_SLOW_DOWN = Caches.getModule(NoSlowDown.class);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, NoSlowDown>
            SLIME_BLOCK = Caches.getSetting
                (NoSlowDown.class, BooleanSetting.class, "Slime", false);

    @Inject(
            method = "onSteppedOn",
            at = @At("HEAD"),
            cancellable = true)
    public void onEntityCollisionHook(CallbackInfo info)
    {
        if (NO_SLOW_DOWN.isEnabled() && SLIME_BLOCK.getValue())
        {
            info.cancel();
        }
    }
}
