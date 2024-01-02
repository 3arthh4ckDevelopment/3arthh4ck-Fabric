package me.earth.earthhack.impl.hud.visual.textradar;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.hud.DynamicHudElement;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

//TODO: filter out bots
public class TextRadar extends DynamicHudElement {

    private final Setting<Boolean> health =
            register(new BooleanSetting("Health", true));
    private final Setting<Boolean> renderDistance =
            register(new BooleanSetting("Distance", false));
    private final Setting<Boolean> pops =
            register(new BooleanSetting("Pops", false));
    private final Setting<Boolean> potions =
            register(new BooleanSetting("Potion-Effects", true));
    private final Setting<Boolean> enemies =
            register(new BooleanSetting("Only-Enemies", false));
    private final Setting<Integer> limit =
            register(new NumberSetting<>("Player-Limit", 7, 1, 25));


    private int counter = 0;

    private void render(DrawContext context, boolean inHud) {
        if (mc.player != null && mc.world != null) {
            Map<String, Integer> players = getTextRadarPlayers();
            if (!players.isEmpty()) {
                float textHeight = Managers.TEXT.getStringHeight();
                float y = getY() - (directionV() == TextDirectionV.BottomToTop ? (players.size() + 1 * textHeight) : 0);
                counter = 0;
                if (directionV() == TextDirectionV.BottomToTop)
                    y += textHeight * (players.size() + 1);
                for (Map.Entry<String, Integer> player : players.entrySet()) {
                    String text = player.getKey() + " ";
                    HudRenderUtil.renderText(context, text, getX() - simpleCalcH(RENDERER.getStringWidth(text)) + simpleCalcH(138.0f), y);
                    if (directionV() == TextDirectionV.BottomToTop)
                        y -= textHeight;
                    else
                        y += textHeight;
                    counter++;
                    if (counter >= limit.getValue())
                        break;
                }
            } else if (inHud) {
                HudRenderUtil.renderText(context, "Text Radar", getX(), getY());
            }
        }
    }

    public Map<String, Integer> getTextRadarPlayers() {
        Map<String, Integer> output = new HashMap<>();
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder healthSB = new StringBuilder();
        StringBuilder distanceSB = new StringBuilder();
        for (PlayerEntity player : Managers.ENTITIES.getPlayers()) {
            if (enemies.getValue() && !Managers.ENEMIES.contains(player))
                break;
            if (player.isInvisible()) {
                continue;
            }

            if (player.getName().equals(mc.player.getName())) {
                continue;
            }
            int hpRaw = (int) EntityUtil.getHealth(player);
            String hp = dfHealth.format(hpRaw);
            healthSB.append(TextColor.SECTIONSIGN);
            if (hpRaw >= 20) {
                healthSB.append("a");
            } else if (hpRaw >= 10) {
                healthSB.append("e");
            } else if (hpRaw >= 5) {
                healthSB.append("6");
            } else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            int distanceInt = (int) mc.player.distanceTo(player);
            String distance = dfDistance.format(distanceInt);
            distanceSB.append(TextColor.SECTIONSIGN);
            if (distanceInt >= 25) {
                distanceSB.append("a");
            } else if (distanceInt > 10) {
                distanceSB.append("6");
            } else if (distanceInt >= 50) {
                distanceSB.append("7"); // Always false!
            } else {
                distanceSB.append("c");
            }
            distanceSB.append(distance);
            output.put(
                    (health.getValue() ? healthSB + " " : "")
                            + (Managers.FRIENDS.contains(player) ? TextColor.AQUA : Managers.ENEMIES.contains(player) ? TextColor.RED : TextColor.RESET)
                            + player.getName()
                            + " "
                            + (renderDistance.getValue() ? distanceSB + " " : "")
                            + TextColor.WHITE
                            + (pops.getValue() ? Managers.COMBAT.getPops(player) : "")
                            + (potions.getValue() ? PotionManager.INSTANCE.getTextRadarPotion(player) : ""),
                    (int) mc.player.distanceTo(player));

            healthSB.setLength(0);
            distanceSB.setLength(0);
        }

        if (!output.isEmpty()) {
            output = sortByValue(output, false);
        }
        return output;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        if(descending) {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        } else {
            list.sort(Map.Entry.comparingByValue());
        }

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public TextRadar() {
        super("TextRadar",  HudCategory.Visual, 2, 54);
        this.setData(new SimpleHudData(this, "Displays enemies near you/in render distance."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context, true);
    }

    @Override
    public void hudDraw(DrawContext context) {
        render(context, false);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate() {
        super.hudUpdate();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return 138.0f;
    }

    @Override
    public float getHeight() {
        if (counter == 0)
            return Managers.TEXT.getStringHeight();
        else
            return Managers.TEXT.getStringHeight() * counter + 1;
    }

}
