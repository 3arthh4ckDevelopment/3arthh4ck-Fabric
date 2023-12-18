package me.earth.earthhack.impl.commands.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CommandGui extends Screen implements Globals
{
    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final Identifier BLACK_PNG =
            new Identifier("earthhack:textures/gui/black.png");
    private static final Identifier GUI_TEXTURES =
            new Identifier("earthhack:textures/gui/gui_textures.png");

    private final Screen parent;
    private TextFieldWidget textField; // = new CommandChatGui("+"); // todo this, works for now so i cba
    private TexturedButtonWidget closeButton;

    public CommandGui(Screen parent)
    {
        super(Text.literal("CommandGui"));
        this.parent = parent;
    }

    @Override
    protected void init()
    {
        closeButton = new TexturedButtonWidget(
                this.width - 20 - 4,
                4,
                20, 20,
                0, 80, 20,
                GUI_TEXTURES,
                256, 256,
                button -> this.client.setScreen(parent));
        textField = new TextFieldWidget(
                this.client.textRenderer,
                2,
                this.height - 10 - 2,
                this.width - 4,
                10,
                Text.of("+"));
        textField.setEditable(true);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1)
        {
            super.charTyped(typedChar, keyCode);
            return false;
        }
        return textField.charTyped(typedChar, keyCode);
    }

    @Override
    public void close()
    {
        this.client.setScreen(parent);
    }

    public void setText(String textIn) {
        this.textField.setText(textIn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        // TODO: overlaps all the drawable children
        // if(BACK.getValue())
        // {
        //     context.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
        //     context.drawTexture(BLACK_PNG, 0, 0, 0, 0.0f, 0.0f, this.width, this.height, 16, 16);
        //     context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        // }
        // else
        // {
        //     renderBackground(context);
        // }
        RenderSystem.enableDepthTest();
        this.addSelectableChild(textField);
        this.addDrawableChild(textField);
        this.addDrawableChild(closeButton);
    }
}
