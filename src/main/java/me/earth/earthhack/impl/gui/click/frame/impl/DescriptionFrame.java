package me.earth.earthhack.impl.gui.click.frame.impl;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.gui.click.frame.Frame;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;

import java.util.List;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class DescriptionFrame extends Frame
{
    public static final ModuleCache<ClickGui> CLICK_GUI = Caches.getModule(ClickGui.class);

    private String description;

    public DescriptionFrame(float posX, float posY, float width, float height)
    {
        this("Description", posX, posY, width, height);
    }

    public DescriptionFrame(String label, float posX, float posY, float width, float height)
    {
        super(label, posX, posY, width, height);
    }

    @Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        if (description == null || !CLICK_GUI.get().description.getValue())
        {
            return;
        }

        super.drawScreen(context, mouseX, mouseY, partialTicks);
        if (CLICK_GUI.get().catEars.getValue()) {
            CategoryFrame.catEarsRender(context, getPosX(), getPosY(), getWidth());
        }
        Render2DUtil.drawRect(context.getMatrices(), getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), CLICK_GUI.get().getTopBgColor().getRGB());
        if (CLICK_GUI.get().getBoxes())
            Render2DUtil.drawBorderedRect(context.getMatrices(), getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), 0.5f, 0, CLICK_GUI.get().getTopColor().getRGB());
        context.drawTextWithShadow(mc.textRenderer, getLabel(), (int) (getPosX() + 3), (int) (getPosY() + getHeight() / 2 - (Managers.TEXT.getStringHeightI() >> 1)), 0xFFFFFFFF);


        float y = this.getPosY() + 2 + (getHeight() / 2) + Managers.TEXT.getStringHeightI();
        List<OrderedText> strings = Managers.TEXT.listFormattedStringToWidth(this.getDescription(), (int) this.getWidth() - 1);

        Render2DUtil.drawRect(context.getMatrices(), getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + 3 + (Managers.TEXT.getStringHeightI() + 1) * strings.size(), 0x92000000);

        for (OrderedText string : strings)
        {
            context.drawTextWithShadow(mc.textRenderer, string, (int) (this.getPosX() + 3), (int) y, CLICK_GUI.get().getTextColorDesc().getRGB());
            y += Managers.TEXT.getStringHeightI() + 1;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        CLICK_GUI.get().descPosX.setValue(getPosX());
        CLICK_GUI.get().descPosY.setValue(getPosY());
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
