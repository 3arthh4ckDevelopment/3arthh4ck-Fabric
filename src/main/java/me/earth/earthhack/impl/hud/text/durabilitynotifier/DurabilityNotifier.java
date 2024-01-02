package me.earth.earthhack.impl.hud.text.durabilitynotifier;

import com.mojang.text2speech.Narrator;
import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static me.earth.earthhack.impl.util.text.NumbersToWords.numberWords;

public class DurabilityNotifier extends HudElement {

    private final Setting<Integer> percentage =
            register(new NumberSetting<>("Percentage", 20, 0, 100));
    private final Setting<Integer> messageDuration =
            register(new NumberSetting<>("Message-Duration", 3, 0, 10));
    private final Setting<Boolean> narratorSetting =
            register(new BooleanSetting("Narrator", false));


    private final Narrator narrator = Narrator.getNarrator();
    private final StopWatch stopWatch = new StopWatch();
    private String text = "[EXAMPLE] Your chestplate is low durability! (20%) [EXAMPLE]";

    private void render() {
        if (mc.player != null && mc.world != null) {
            if (stopWatch.getTime() < messageDuration.getValue() * 1000) {
                HudRenderUtil.renderText(text, getX(), getY());
            }
        }
    }

    public DurabilityNotifier() {
        super("DurabilityNotifier", HudCategory.Text, 230, 70);
        this.setData(new SimpleHudData(this, "Displays your FPS"));

        String[][] armorPieces = {{"boots", "0"},{"leggings", "0"},{"chestplate", "0"},{"helmet", "0"}};

        this.listeners.add(new LambdaListener<>(TickEvent.class, e -> {
            if (mc.player == null || mc.world == null) {return;}
            for (int i = 3; i >= 0; i--) {
                ItemStack stack = mc.player.inventory.armor.get(i);
                if (!stack.isEmpty()
                        && (stack.getMaxDamage() - stack.getDamage()) * 100 / stack.getMaxDamage() <= percentage.getValue()
                        && !Objects.equals(armorPieces[i][1], "1")) {
                    if (narratorSetting.getValue()) {
                        narrator.clear();
                        narrator.say("Your" + armorPieces[i][0] + " has " + numberWords.get((stack.getMaxDamage() - stack.getDamage())) + " durability left!", false);
                        text = armorPieces[i][0] + " has low durability! (" + numberWords.get((stack.getMaxDamage() - stack.getDamage())) + "%)";
                        stopWatch.reset();
                    }
                    armorPieces[i][1] = "1";
                } else if (stack.isEmpty() && Objects.equals(armorPieces[i][1], "1")) {
                    armorPieces[i][1] = "0";
                }
            }
        }));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        text = "[EXAMPLE] Your chestplate is low durability! (20%) [EXAMPLE]";
        HudRenderUtil.renderText(text, getX(), getY());
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
