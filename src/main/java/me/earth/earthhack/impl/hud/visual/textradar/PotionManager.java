package me.earth.earthhack.impl.hud.visual.textradar;

import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PotionManager {

    public static final PotionManager INSTANCE = new PotionManager();
    private final Map<PlayerEntity, PotionList> potions = new ConcurrentHashMap<>();

    public List<StatusEffectInstance> getPlayerPotions(PlayerEntity player) {
        PotionList list = potions.get(player);
        List<StatusEffectInstance> potions = new ArrayList<>();
        if (list != null) {
            potions = list.getEffects();
        }
        return potions;
    }
    public static class PotionList {
        private final List<StatusEffectInstance> effects = new ArrayList<>();

        public void addEffect(StatusEffectInstance effect) {
            if (effect != null) {
                this.effects.add(effect);
            }
        }

        public List<StatusEffectInstance> getEffects() {
            return this.effects;
        }
    }


    public StatusEffectInstance[] getImportantPotions(PlayerEntity player) {
        StatusEffectInstance[] array = new StatusEffectInstance[3];
        for (StatusEffectInstance effect : getPlayerPotions(player)) {
            switch(effect.getEffectType().getName().getString().toLowerCase()) {
                case "strength" :
                    array[0] = effect;
                    break;
                case "weakness" :
                    array[1] = effect;
                    break;
                case "speed" :
                    array[2] = effect;
                    break;
                default:
            }
        }
        return array;
    }

    public String getTextRadarPotion(PlayerEntity player) {
        StatusEffectInstance[] array = getImportantPotions(player);
        StatusEffectInstance strength = array[0];
        StatusEffectInstance weakness = array[1];
        StatusEffectInstance speed = array[2];
        return (strength != null ? TextColor.RED + " S" + (strength.getAmplifier() + 1) + " " : "")
                + (weakness != null ? TextColor.DARK_GRAY + " W " : "")
                + (speed != null ? TextColor.AQUA + " S" + (speed.getAmplifier() + 1) + " " : "");
    }

}
