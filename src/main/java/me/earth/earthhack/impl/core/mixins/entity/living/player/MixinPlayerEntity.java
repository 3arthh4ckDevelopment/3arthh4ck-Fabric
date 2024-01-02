package me.earth.earthhack.impl.core.mixins.entity.living.player;

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
