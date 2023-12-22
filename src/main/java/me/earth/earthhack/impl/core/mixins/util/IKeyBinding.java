package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for {@link net.minecraft.client.option.KeyBinding}.
 */
@Mixin(KeyBinding.class)
public interface IKeyBinding
{
    @Accessor(value = "boundKey")
    public InputUtil.Key getBoundKey();
}
