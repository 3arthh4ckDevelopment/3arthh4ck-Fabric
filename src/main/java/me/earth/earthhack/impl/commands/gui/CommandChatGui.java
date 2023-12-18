package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;

public class CommandChatGui extends ChatScreen
{
    public CommandChatGui(String originalChatText)
    {
        super(originalChatText);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1)
        {
            return false;
        }
        else if (keyCode == 28 || keyCode == 156)
        {
            String s = this.chatField.getText().trim();

            if (!s.isEmpty())
            {
                this.sendMessage(s, true);
            }

            return false;
        }

        return super.charTyped(typedChar, keyCode);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.chatField.getText().startsWith(Commands.getPrefix()))
        {
            this.chatField.setText(Commands.getPrefix()
                    + chatField.getText());
        }

        super.render(context, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean sendMessage(String msg, boolean addToChat)
    {
        this.client.inGameHud.getChatHud().addToMessageHistory(msg);
        this.setText(Commands.getPrefix());
        Managers.COMMANDS.applyCommand(msg);
        return addToChat;
    }

    public void setFieldText(String text)
    {
        this.chatField.setText(text);
    }
}
