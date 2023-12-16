package me.earth.earthhack.impl.commands.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CommandGui extends Screen implements Globals
{
    private static final SettingCache<Boolean, BooleanSetting, Commands> BACK =
            Caches.getSetting(Commands.class, BooleanSetting.class, "BackgroundGui", false);
    private static final Identifier BLACK_PNG =
            new Identifier("earthhack:textures/gui/black.png");

    protected TextFieldWidget chatField;
    ChatInputSuggestor chatInputSuggestor;
    private final Screen parent;

    public CommandGui(Screen parent)
    {
        super(Text.literal("CommandGui"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.chatField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, Text.translatable("chat.editBox")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(chatInputSuggestor.getNarration());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText("+");
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.chatField.setFocusUnlocked(false);
        this.addSelectableChild(this.chatField);
        this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.setInitialFocus(this.chatField);
    }

    private void onChatFieldUpdate(String chatText) {
        String string = this.chatField.getText();
        this.chatInputSuggestor.setWindowActive(!string.equals("+"));
        this.chatInputSuggestor.refresh();
    }

    public void setText(String text)
    {
        this.chatField.setText(text);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1)
        {
            super.charTyped(typedChar, keyCode);
            return false;
        }

        this.chatField.charTyped(typedChar, keyCode);
        return false;
    }

    @Override
    public void close()
    {
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        super.render(context, mouseX, mouseY, partialTicks);
        Window wnd = this.client.getWindow();

        if (BACK.getValue())
        {
            this.renderBackgroundTexture(context);
        }
        else
        {
            RenderSystem.disableCull();
            RenderSystem.setShaderFogEnd(0);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(BLACK_PNG);
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferbuilder.vertex(0.0D, this.height, 0.0D).texture((float) 0.0D, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).next();
            bufferbuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F + (float)0).color(64, 64, 64, 255).next();
            bufferbuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / 32.0F, 0).color(64, 64, 64, 255).next();
            bufferbuilder.vertex(0.0D, 0.0D, 0.0D).texture((float) 0.0D, 0).color(64, 64, 64, 255).next();
            tessellator.draw();
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SrcFactor.ONE,
                GlStateManager.DstFactor.ZERO
        );
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, wnd.getScaledHeight() - 48, 0.0f);
        mc.inGameHud.getChatHud().render(context, mc.inGameHud.getTicks(), mouseX, mouseY);
        GL11.glPopMatrix();
        this.chatField.render(context, mouseX, mouseY, partialTicks);
        super.render(context, mouseX, mouseY, partialTicks);
    }
}
