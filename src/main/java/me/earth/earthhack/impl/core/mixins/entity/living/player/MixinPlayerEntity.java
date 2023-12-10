package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.modules.Caches;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    // private static final ModuleCache<TpsSync> TPS_SYNC =
    //         Caches.getModule(TpsSync.class);
    // private static final SettingCache<Boolean, BooleanSetting, TpsSync> ATTACK =
    //         Caches.getSetting(TpsSync.class, BooleanSetting.class, "Attack", false);

    @Shadow
    public void tick()
    {
        throw new IllegalStateException("onUpdate was not shadowed!");
    }

}
