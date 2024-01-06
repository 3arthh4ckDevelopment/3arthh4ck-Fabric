package me.earth.earthhack.impl.modules.misc.antipotion;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Set;

public class AntiPotion extends Module
{
    public AntiPotion()
    {
        super("AntiPotion", Category.Misc);
        AntiPotionData data = new AntiPotionData(this);
        this.setData(data);
        for (StatusEffect effect : effects)
        {
            boolean value = effect == StatusEffects.LEVITATION;
            String name = getPotionString(effect);
            Setting<?> s = register(
                    new BooleanSetting(name, value));

            data.register(s, "Removes " + name + " potion effects.");
        }

        this.listeners.add(new ListenerUpdates(this));
    }

    public static String getPotionString(StatusEffect effect)
    {
        return effect.getName().getString();
    }

    private static final Set<StatusEffect> effects = Sets.newHashSet(
            StatusEffects.SPEED,
            StatusEffects.SLOWNESS,
            StatusEffects.HASTE,
            StatusEffects.MINING_FATIGUE,
            StatusEffects.STRENGTH,
            StatusEffects.INSTANT_HEALTH,
            StatusEffects.INSTANT_DAMAGE,
            StatusEffects.JUMP_BOOST,
            StatusEffects.NAUSEA,
            StatusEffects.REGENERATION,
            StatusEffects.RESISTANCE,
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.WATER_BREATHING,
            StatusEffects.INVISIBILITY,
            StatusEffects.BLINDNESS,
            StatusEffects.NIGHT_VISION,
            StatusEffects.HUNGER,
            StatusEffects.WEAKNESS,
            StatusEffects.POISON,
            StatusEffects.WITHER,
            StatusEffects.HEALTH_BOOST,
            StatusEffects.ABSORPTION,
            StatusEffects.SATURATION,
            StatusEffects.GLOWING,
            StatusEffects.LEVITATION,
            StatusEffects.LUCK,
            StatusEffects.UNLUCK,
            StatusEffects.SLOW_FALLING,
            StatusEffects.CONDUIT_POWER,
            StatusEffects.DOLPHINS_GRACE,
            StatusEffects.BAD_OMEN,
            StatusEffects.HERO_OF_THE_VILLAGE,
            StatusEffects.DARKNESS
    );

}
