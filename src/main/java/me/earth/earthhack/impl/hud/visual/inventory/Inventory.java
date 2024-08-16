package me.earth.earthhack.impl.hud.visual.inventory;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.commands.KitCommand;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.shulker.ShulkerNBTUtil;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class Inventory extends HudElement {

    private final Setting<Boolean> xCarry =
            register(new BooleanSetting("XCarry", false));
    private final Setting<HudBox> box =
            register(new EnumSetting<>("Box", HudBox.Regular));
    private final Setting<Color> boxColor =
            register(new ColorSetting("BoxColor", new Color(23,23,23,23)));
    private final Setting<Color> outlineColor =
            register(new ColorSetting("OutlineColor", new Color(23,23,23,23)));


    protected void onRender(DrawContext context) {
        if (box.getValue() != HudBox.None) {
            if (box.getValue().equals(HudBox.Rounded))
                Render2DUtil.roundedRect(context.getMatrices(), getX(), getY() - 1.0f,
                        getX() + 9 * 18, getY() + 55.0f, 2, boxColor.getValue().getRGB());
            else
                Render2DUtil.drawBorderedRect(context.getMatrices(), getX(), getY(), getX() + 9 * 18,
                        getY() + 55.0f, 1.0f, boxColor.getValue().getRGB(), outlineColor.getValue().getRGB());
        }

        if (this.isGui() && mc.player.getInventory().main.stream().filter(x -> !x.isEmpty()).toList().isEmpty()) {
            DefaultedList<ItemStack> items = ShulkerNBTUtil.getShulkerItemList(KitCommand.KIT);
            if (items != null) {
                Managers.TEXT.drawString(context, "Example Inventory", (int) getX(), (int) getY() - 5, 0xffffffff);
                Render2DUtil.drawItemsInventory(context, items, (int) getX(), (int) getY());
            }
        } else {
            Render2DUtil.drawItemsInventory(context, mc.player.getInventory().main, (int) getX(), (int) getY());
            if (xCarry.getValue()) {
                for (int i = 1; i < 5; i++) {
                    int iX = (int) getX() + ((i + 4) % 9) * (18);
                    ItemStack itemStack = mc.player.getInventory().getStack(i);
                    if (!itemStack.isEmpty())
                        Render2DUtil.drawItem(context, itemStack, iX, (int) getY(), true);
                }
            }
        }
    }

    public Inventory() {
        super("Inventory", "Displays your inventory",  HudCategory.Visual, 80, 100);
    }

    @Override
    public float getWidth() {
        return 9 * 18;
    }

    @Override
    public float getHeight() {
        return 55.0f;
    }

    public enum HudBox {
        Regular,
        Rounded,
        None
    }
}
