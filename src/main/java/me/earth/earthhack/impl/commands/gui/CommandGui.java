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
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

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
        //noinspection ConstantConditions
        closeButton = new TexturedButtonWidget(
                this.width - 20 - 4,
                4,
                20, 20,
                new ButtonTextures(GUI_TEXTURES, GUI_TEXTURES),
                button -> client.setScreen(parent));
        //noinspection ConstantConditions
        textField = new TextFieldWidget(
                this.client.textRenderer,
                2,
                this.height - 10 - 2,
                this.width - 4,
                10,
                Text.of("+"));
        textField.setEditable(true);
        textField.setFocused(true);
        textField.setText(Commands.getPrefix());
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

        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, 0.0f, this.height, -111).color(64, 64, 64, 255).texture(0, (float)this.height / 32.0F + (float) 0).next();
        bufferBuilder.vertex(matrix4f, this.width, this.height, -111).color(64, 64, 64, 255).texture((float)this.width / 32.0F, (float) this.height / 32.0F + (float)0).next();
        bufferBuilder.vertex(matrix4f, this.width, 0.0f, -111).color(64, 64, 64, 255).texture((float)this.width / 32.0F, 0).next();
        bufferBuilder.vertex(matrix4f, 0.0f, 0.0f, -111).color(64, 64, 64, 255).texture(0, 0).next();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, BLACK_PNG);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        tessellator.draw();

        // RenderSystem.enableBlend();
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
        //RenderSystem.enableDepthTest();
        this.addSelectableChild(textField);
        this.addDrawableChild(textField);
        this.addDrawableChild(closeButton);
    }
}
