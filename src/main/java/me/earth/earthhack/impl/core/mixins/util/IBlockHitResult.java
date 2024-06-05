package me.earth.earthhack.impl.core.mixins.util;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockHitResult.class)
public interface IBlockHitResult {
    @Accessor(value = "side")
    void earthhack$setDirection(Direction direction);

}
