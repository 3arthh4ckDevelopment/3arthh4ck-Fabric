package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Iterator;

public class HelpCommand extends Command implements Globals
{
    public HelpCommand()
    {
        super(new String[][]{{"help"}});
        CommandDescriptions.register(this,
                "Get a list and help for all commands.");
    }

    @Override
    public void execute(String[] args)
    {
        MutableText component =
                Text.literal("Following commands are available: ");

        Iterator<Command> it = Managers.COMMANDS.getRegistered().iterator();
        while (it.hasNext())
        {
            Command command = it.next();
            if (command != null)
            {
                MutableText sibling =
                        Text.literal(TextColor.AQUA
                                                + command.getName()
                                                + TextColor.WHITE
                                                + (it.hasNext() ? ", " : ""));

                String descr = CommandDescriptions.getDescription(command);
                HoverEvent event = new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.of(descr == null
                                                    ? "A command."
                                                    : descr));
                ChatComponentUtil.setOffset(event);
                Style style = Style.EMPTY.withHoverEvent(event);
                if (command instanceof ModuleCommand)
                {
                    style.withClickEvent(new RunnableClickEvent(() ->
                        setText(Commands.getPrefix() + "AutoCrystal")));
                }
                else
                {
                    style.withClickEvent(new RunnableClickEvent(() ->
                        setText(Commands.getPrefix() + command.getName())));
                }

                sibling.setStyle(style);
                component.append(sibling);
            }
        }

        ChatUtil.sendMessage(component.getString(), getName());
    }
    private void setText(String text)
    {
        Screen current = mc.currentScreen;
        if (current instanceof CommandGui)
        {
            ((CommandGui) current).setText(text);
        }
        else
        {
            mc.setScreen(new ChatScreen(text));
        }
    }

}
