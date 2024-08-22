package me.earth.earthhack.impl.core.mixins.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FoodComponent.class)
public interface IFoodComponent
{
    @Accessor("effects")
    List<Pair<StatusEffectInstance, Float>> getStatusEffects();
}
