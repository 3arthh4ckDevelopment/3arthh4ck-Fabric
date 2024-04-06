package me.earth.earthhack.impl.hud.impl.visual.pvpresources;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.ArrayList;

public class PvpResources extends HudElement {

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Simple));
    private final Setting<Styles> style =
            register(new EnumSetting<>("Style", Styles.Vertical));
    private final Setting<Boolean> pretty = // TODO use HudBox for this (this is autistic so i'm not doing it rn)
            register(new BooleanSetting("Pretty", true));
    private final Setting<Color> color =
            register(new ColorSetting("Color", new Color(0, 0, 0, 0)));
    private final Setting<Boolean> obby =
            register(new BooleanSetting("Obsidian", false));
    private final Setting<String> blocks =
            register(new StringSetting("Add/Remove", "Add/Remove"));

    ArrayList<Item> items = new ArrayList<>();
    Item[] defaultIds = {Items.END_CRYSTAL, Items.EXPERIENCE_BOTTLE, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING};
    int x = 0;
    int y = 0;
    int finalOffset;
    
    private void render(DrawContext context) {
        if (mc.player != null) {
            if (mode.getValue() == Mode.Simple) {
                for (Item i : defaultIds)
                    if (!items.contains(i))
                        items.add(i);
            }
            if (obby.getValue()) {
                if (!items.contains(Items.OBSIDIAN))
                    items.add(Items.OBSIDIAN);
            } else {
                items.remove(Items.OBSIDIAN);
            }


            x = (int) getX();
            y = (int) getY();

            if (style.getValue() == Styles.Square)
                drawSquare(context);
            else if (style.getValue() == Styles.Vertical)
                drawVertical(context);
            else
                drawHorizontal(context);
        }
    }

    private void drawVertical(DrawContext context) {
        finalOffset = items.size() * 20;
        if (pretty.getValue())
            Render2DUtil.roundedRect(context.getMatrices(), x, y - 1, x + 16, y + finalOffset - 3, 2.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(context.getMatrices(), x - 3, y - 3, x + 20, y + finalOffset, color.getValue().getRGB());

        int offset = 0;
        for (Item i : items) {
            renderItem(context, i, x, y + offset);
            offset += 20;
        }
    }

    private void drawSquare(DrawContext context) {
        if (pretty.getValue())
            Render2DUtil.roundedRect(context.getMatrices(), x + 1, y + 1, x + 36, y + 36, 4.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(context.getMatrices(), x - 3, y - 3, x + 40, y + 40, color.getValue().getRGB());


        renderItem(context, Items.END_CRYSTAL, x, y);
        renderItem(context, Items.EXPERIENCE_BOTTLE, x, y + 20);
        renderItem(context, Items.ENCHANTED_GOLDEN_APPLE, x + 20, y);
        renderItem(context, Items.TOTEM_OF_UNDYING, x + 20, y + 20);
    }

    private void drawHorizontal(DrawContext context) {
        finalOffset = items.size() * 20;
        if (pretty.getValue())
            Render2DUtil.roundedRect(context.getMatrices(), x - 1, y + 1, x + finalOffset - 3, y + 16, 2.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(context.getMatrices(), x - 2, y - 2, x + finalOffset - 1, y + 18, color.getValue().getRGB());

        int offset = 0;
        for (Item i : items) {
            renderItem(context, i, x + offset, y);
            offset += 20;
        }
    }

    public void renderItem(DrawContext context, Item item, int xPosition, int yPosition) {
        Managers.TEXT.drawStringWithShadow(context, getItemCount(item), xPosition + 18 - Managers.TEXT.getStringWidth(getItemCount(item)), yPosition + 9, -1);
        context.drawItem(item.getDefaultStack(), xPosition, yPosition, 100205, (int) getZ());
    }

    public static String getItemCount(Item item) {
        int itemCount = mc.player.getInventory().main.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum() + ((mc.player.getOffHandStack().getItem() == item)
                ? mc.player.getOffHandStack().getCount() : 0);
        if (itemCount >= 1000)
            return Integer.toString(itemCount).charAt(0) + "." + Integer.toString(itemCount).charAt(1) + "K";
        else
            return Integer.toString(itemCount);
    }

    public PvpResources() {
        super("Items", HudCategory.Visual, 60, 70);

        this.blocks.addObserver(event -> {
            if (!event.isCancelled()) {
                Item item = ItemAddingModule.getItemStartingWith(event.getValue(), i -> true);
                if (item != null) {
                    if (!items.contains(item))
                        items.add(item);
                    else
                        items.remove(item);
                }
            }
        });

        this.mode.addObserver(event -> items.clear());

        this.setData(new SimpleHudData(this, "Displays some items from your Inventory."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
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
        if (mode.getValue() == Mode.Extended && items.isEmpty())
            return 17;
        else
            return style.getValue() == Styles.Horizontal
                    ? finalOffset
                    : style.getValue() == Styles.Square
                        ? 37
                        : 17;
    }

    @Override
    public float getHeight() {
        if (mode.getValue() == Mode.Extended && items.isEmpty())
            return 17;
        else
            return style.getValue() == Styles.Vertical
                    ? finalOffset
                    : style.getValue() == Styles.Square
                        ? 37
                        : 20;
    }

    private enum Styles {
        Vertical,
        Horizontal,
        Square
    }

    private enum Mode {
        Simple,
        Extended
    }

}
