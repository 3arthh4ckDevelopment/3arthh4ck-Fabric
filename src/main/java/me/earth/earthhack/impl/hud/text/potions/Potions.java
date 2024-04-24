package me.earth.earthhack.impl.hud.text.potions;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.hud.DynamicHudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.editor.HudEditor;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.render.hud.HudRainbow;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// TODO: hud potion symbols
// TODO: Rewrite @Ai_2473 ....
public class Potions extends DynamicHudElement {

    private static final Potions INSTANCE = new Potions();

    private static final ModuleCache<HudEditor> HUD_EDITOR = Caches.getModule(HudEditor.class);

    private final Setting<PotionColor> potionColor =
            register(new EnumSetting<>("PotionColor", PotionColor.Phobos));
    private final Setting<Integer> textOffset =
            register(new NumberSetting<>("Offset", 2, 0, 10));

    private final String label = "[Potions] No effects applied."; // render this?
    int effCounter = 0;

    private void render(DrawContext context, boolean isHud) {
        if (mc.player != null) {
            ArrayList<StatusEffectInstance> sorted = new ArrayList<>(mc.player.getStatusEffects());
            effCounter = mc.player.getStatusEffects().size();

            sorted.sort(Comparator.comparingDouble(effect -> -RENDERER.getStringWidth(effect.getEffectType().getName().getString() + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "") + Formatting.GRAY + " " + getPotionDuration(effect))));
            int offset = 0;
            float yPos = (directionV() == TextDirectionV.BottomToTop ? getY() + (sorted.size() * (Managers.TEXT.getStringHeight() + textOffset.getValue())) - Managers.TEXT.getStringHeightI() : getY());
            float borderDistance = simpleCalcH(getWidth());
            if (!sorted.isEmpty()) {
                for (StatusEffectInstance effect : sorted) {
                    if (effect != null) {
                        final String label = effect.getEffectType().getName().getString()
                                + (effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "")
                                + Formatting.GRAY + " " + getPotionDuration(effect);

                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        float xPos = getX() - simpleCalcH(RENDERER.getStringWidth(label));
                        if (directionV() == TextDirectionV.BottomToTop)
                            renderPotionText(context, label, borderDistance + xPos, yPos - offset - animationY, effect.getEffectType());
                        else
                            renderPotionText(context, label, borderDistance + xPos, yPos + offset + animationY, effect.getEffectType());
                        offset += RENDERER.getStringHeightI() + textOffset.getValue();
                    }
                }
            } else if (isHud) {
                HudRenderUtil.renderText(context, label, getX(), getY());
            }
        }
    }

    private String getPotionDuration(StatusEffectInstance effect) {
        if (effect.isInfinite())
            return "**:**";
        else {
            int totalTime = effect.getDuration();
            totalTime /= 20;
            // h:mm:ss
            String hours = totalTime / 3600 != 0 ? String.valueOf(totalTime / 3600) : "";
            String m = (totalTime % 3600) / 60 != 0 ? String.valueOf((totalTime % 3600) / 60) : "0";
            String minutes = (hours != "" ? (m.length() == 1 ? "0" : "") : "") + m;
            String seconds = (totalTime % 60 < 10 ? "0" : "") + totalTime % 60;
            return hours + (hours != "" ? ":" : "") + minutes + ":" + seconds;
        }
    }

    public void renderPotionText(DrawContext context, String text, float x, float y, StatusEffect effect) {
        String colorCode = (potionColor.getValue() == PotionColor.OldVersions || potionColor.getValue() == PotionColor.Phobos || potionColor.getValue() == PotionColor.Normal) ? "" : HUD_EDITOR.get().colorMode.getValue().getColor();
        RENDERER.drawStringWithShadow(context, colorCode + text, x, y, getPotionColor(effect, y));
    }

    private int getPotionColor(StatusEffect effect, float y) {
        if (potionColor.getValue() == PotionColor.OldVersions || potionColor.getValue() == PotionColor.Phobos)
            return potionColorMap.get(effect).getRGB();
        else if (potionColor.getValue() == PotionColor.Normal)
            return effect.getColor();
        else
            return HUD_EDITOR.get().colorMode.getValue() == HudRainbow.None
                    ? HUD_EDITOR.get().color.getValue().getRGB()
                    : (HUD_EDITOR.get().colorMode.getValue() == HudRainbow.Static ? (ColorUtil.staticRainbow((y + 1) * 0.89f, HUD_EDITOR.get().color.getValue())) : 0xffffffff);
    }

    void oldVerColors() {
        potionColorMap.clear();
        potionColorMap.put(StatusEffects.SPEED, new Color(85, 255, 255));
        potionColorMap.put(StatusEffects.SLOWNESS, new Color(0, 0, 0));
        potionColorMap.put(StatusEffects.HASTE, new Color(255, 170, 0));
        potionColorMap.put(StatusEffects.MINING_FATIGUE, new Color(74, 66, 23));
        potionColorMap.put(StatusEffects.STRENGTH, new Color(255, 85, 85));
        potionColorMap.put(StatusEffects.INSTANT_HEALTH, new Color(67, 10, 9));
        potionColorMap.put(StatusEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
        potionColorMap.put(StatusEffects.JUMP_BOOST, new Color(85, 255, 255));
        potionColorMap.put(StatusEffects.NAUSEA, new Color(85, 29, 74));
        potionColorMap.put(StatusEffects.REGENERATION, new Color(255, 85, 255));
        potionColorMap.put(StatusEffects.RESISTANCE, new Color(255, 85, 85));
        potionColorMap.put(StatusEffects.FIRE_RESISTANCE, new Color(255, 170, 0));
        potionColorMap.put(StatusEffects.WATER_BREATHING, new Color(46, 82, 153));
        potionColorMap.put(StatusEffects.INVISIBILITY, new Color(127, 131, 146));
        potionColorMap.put(StatusEffects.BLINDNESS, new Color(31, 31, 35));
        potionColorMap.put(StatusEffects.NIGHT_VISION, new Color(85, 255, 85));
        potionColorMap.put(StatusEffects.HUNGER, new Color(88, 118, 83));
        potionColorMap.put(StatusEffects.WEAKNESS, new Color(0, 0, 0));
        potionColorMap.put(StatusEffects.POISON, new Color(85, 255, 85));
        potionColorMap.put(StatusEffects.WITHER, new Color(0, 0, 0));
        potionColorMap.put(StatusEffects.HEALTH_BOOST, new Color(248, 125, 35));
        potionColorMap.put(StatusEffects.ABSORPTION, new Color(85, 85, 255));
        potionColorMap.put(StatusEffects.SATURATION, new Color(248, 36, 35));
        potionColorMap.put(StatusEffects.GLOWING, new Color(148, 160, 97));
        potionColorMap.put(StatusEffects.LEVITATION, new Color(206, 255, 255));
        potionColorMap.put(StatusEffects.LUCK, new Color(51, 153, 0));
        potionColorMap.put(StatusEffects.UNLUCK, new Color(192, 164, 77));
    }

    private final Map<StatusEffect, Color> potionColorMap = new HashMap<>();
    public Potions() {
        super("PotionEffects", HudCategory.Text, 120, 120);
        this.setData(new SimpleHudData(this, "Displays active potion effects."));

        oldVerColors();

        this.potionColor.addObserver(e -> {
            System.out.println(potionColorMap.size());
            if (potionColor.getValue() != PotionColor.OldVersions) {
                oldVerColors();
            } else if (potionColor.getValue() == PotionColor.OldVersions) {
                potionColorMap.clear();
                potionColorMap.put(StatusEffects.SPEED, new Color(124, 175, 198));
                potionColorMap.put(StatusEffects.SLOWNESS, new Color(90, 108, 129));
                potionColorMap.put(StatusEffects.HASTE, new Color(217, 192, 67));
                potionColorMap.put(StatusEffects.MINING_FATIGUE, new Color(74, 66, 23));
                potionColorMap.put(StatusEffects.STRENGTH, new Color(147, 36, 35));
                potionColorMap.put(StatusEffects.INSTANT_HEALTH, new Color(67, 10, 9));
                potionColorMap.put(StatusEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
                potionColorMap.put(StatusEffects.JUMP_BOOST, new Color(34, 255, 76));
                potionColorMap.put(StatusEffects.NAUSEA, new Color(85, 29, 74));
                potionColorMap.put(StatusEffects.REGENERATION, new Color(205, 92, 171));
                potionColorMap.put(StatusEffects.RESISTANCE, new Color(153, 69, 58));
                potionColorMap.put(StatusEffects.FIRE_RESISTANCE, new Color(228, 154, 58));
                potionColorMap.put(StatusEffects.WATER_BREATHING, new Color(46, 82, 153));
                potionColorMap.put(StatusEffects.INVISIBILITY, new Color(127, 131, 146));
                potionColorMap.put(StatusEffects.BLINDNESS, new Color(31, 31, 35));
                potionColorMap.put(StatusEffects.NIGHT_VISION, new Color(31, 31, 161));
                potionColorMap.put(StatusEffects.HUNGER, new Color(88, 118, 83));
                potionColorMap.put(StatusEffects.WEAKNESS, new Color(72, 77, 72));
                potionColorMap.put(StatusEffects.POISON, new Color(78, 147, 49));
                potionColorMap.put(StatusEffects.WITHER, new Color(53, 42, 39));
                potionColorMap.put(StatusEffects.HEALTH_BOOST, new Color(248, 125, 35));
                potionColorMap.put(StatusEffects.ABSORPTION, new Color(37, 82, 165));
                potionColorMap.put(StatusEffects.SATURATION, new Color(248, 36, 35));
                potionColorMap.put(StatusEffects.GLOWING, new Color(148, 160, 97));
                potionColorMap.put(StatusEffects.LEVITATION, new Color(206, 255, 255));
                potionColorMap.put(StatusEffects.LUCK, new Color(51, 153, 0));
                potionColorMap.put(StatusEffects.UNLUCK, new Color(192, 164, 77));
            } else {
                potionColorMap.clear();
            }
        });
    }

    public static Potions getInstance() {
        return INSTANCE;
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context, true);
    }

    @Override
    public void draw(DrawContext context) {
        render(context, false);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(label);
    }

    @Override
    public float getHeight() {
        return (Managers.TEXT.getStringHeight() + textOffset.getValue()) * (effCounter == 0 ? 1 : effCounter);
    }

}