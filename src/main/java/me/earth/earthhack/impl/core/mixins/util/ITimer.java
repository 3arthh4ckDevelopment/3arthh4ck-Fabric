package me.earth.earthhack.impl.core.mixins.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.timer.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface ITimer
{
    @Accessor(value = "tickLength")
    void setTickLength(float length);
}
