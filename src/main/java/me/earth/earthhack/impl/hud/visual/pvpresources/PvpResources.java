package me.earth.earthhack.impl.hud.visual.pvpresources;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;

public class PvpResources extends HudElement {

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Simple));
    private final Setting<Styles> style =
            register(new EnumSetting<>("Style", Styles.Vertical));
    private final Setting<Boolean> pretty =
            register(new BooleanSetting("Pretty", true));
    private final Setting<Color> color =
            register(new ColorSetting("Color", new Color(0, 0, 0, 0)));
    private final Setting<Boolean> obby =
            register(new BooleanSetting("Obsidian", false));
    private final Setting<String> blocks =
            register(new StringSetting("Add/Remove", "Add/Remove"));

    ArrayList<Integer> blockIds = new ArrayList<>();
    int[] defaultIds = {426, 384, 322, 449};
    int x = 0;
    int y = 0;
    int finalOffset;
    
    private void render() {
        if (mc.player != null) {
            if (mode.getValue() == Mode.Simple) {
                for (int I : defaultIds)
                    if (!blockIds.contains(I))
                        blockIds.add(I);
            }
            if (obby.getValue()) {
                if (!blockIds.contains(49))
                    blockIds.add(49);
            } else if (blockIds.contains(49))
                blockIds.remove((Object) 49);


            x = (int) getX();
            y = (int) getY();

            if (style.getValue() == Styles.Square)
                drawSquare();
            else if (style.getValue() == Styles.Vertical)
                drawVertical();
            else
                drawHorizontal();
        }
    }

    private void drawVertical() {
        finalOffset = blockIds.size() * 20;
        if (pretty.getValue())
            Render2DUtil.roundedRect(getContext().getMatrices(), x, y - 1, x + 16, y + finalOffset - 3, 2.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(getContext().getMatrices(), x - 3, y - 3, x + 20, y + finalOffset, color.getValue().getRGB());

        int offset = 0;
        for (int I : blockIds) {
            renderItem(I, x, y + offset);
            offset += 20;
        }
    }

    private void drawSquare() {
        if (pretty.getValue())
            Render2DUtil.roundedRect(getContext().getMatrices(), x + 1, y + 1, x + 36, y + 36, 4.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(getContext().getMatrices(), x - 3, y - 3, x + 40, y + 40, color.getValue().getRGB());


        renderItem(426, x, y);
        renderItem(384, x, y + 20);
        renderItem(322, x + 20, y);
        renderItem(449, x + 20, y + 20);
    }

    private void drawHorizontal() {
        finalOffset = blockIds.size() * 20;
        if (pretty.getValue())
            Render2DUtil.roundedRect(getContext().getMatrices(), x - 1, y + 1, x + finalOffset - 3, y + 16, 2.0f, color.getValue().getRGB());
        else
            Render2DUtil.drawRect(getContext().getMatrices(), x - 2, y - 2, x + finalOffset - 1, y + 18, color.getValue().getRGB());

        int offset = 0;
        for (int I : blockIds) {
            renderItem(I, x + offset, y);
            offset += 20;
        }
    }

    public void renderItem(int itemId, int xPosition, int yPosition) {
        getContext().drawItem(new ItemStack(Item.byRawId(itemId), 1), xPosition, yPosition, 100205, (int) getZ());
        Managers.TEXT.drawStringWithShadow(getItemCount(Item.byRawId(itemId)), xPosition + 18 - Managers.TEXT.getStringWidth(getItemCount(Item.byRawId(itemId))), yPosition + 9, -1);
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
                    int itemId = Item.getRawId(item);
                    if (!blockIds.contains(itemId))
                        blockIds.add(itemId);
                    else
                        blockIds.remove((Object)itemId);
                }
            }
        });

        this.mode.addObserver(event -> blockIds.clear());

        this.setData(new SimpleHudData(this, "Displays some items from your Inventory."));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
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
        if (mode.getValue() == Mode.Extended && blockIds.isEmpty())
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
        if (mode.getValue() == Mode.Extended && blockIds.isEmpty())
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
