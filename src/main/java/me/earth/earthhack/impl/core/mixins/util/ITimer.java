package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTickCounter.Dynamic.class)
public interface ITimer
{
    @Accessor(value = "lastFrameDuration")
    void setTickLength(float length);
}
