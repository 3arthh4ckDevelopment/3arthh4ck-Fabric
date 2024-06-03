package me.earth.earthhack.impl.modules.misc.antipotion;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;

import java.util.Set;

public class AntiPotion extends Module
{
    public AntiPotion()
    {
        super("AntiPotion", Category.Misc);
        AntiPotionData data = new AntiPotionData(this);
        this.setData(data);
        for (StatusEffect effect : Registries.STATUS_EFFECT.stream().toList())
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
}
