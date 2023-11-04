package me.earth.earthhack.impl.managers.chat;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.register.Register;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.ModuleCommand;
import me.earth.earthhack.impl.commands.hidden.FailCommand;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class CommandManager extends SubscriberImpl
        implements Globals, Register<Command>
{

    private static final Command MODULE_COMMAND = new ModuleCommand();
    private static final Command FAIL_COMMAND   = new FailCommand();

    private final Set<Command> commands = new LinkedHashSet<>();
    private final Set<Command> hidden   = new LinkedHashSet<>();
    private String concatenated;
    private String lastMessage;

    public CommandManager()
    {
        this.listeners.add(
                new EventListener<PacketEvent.Send<ChatMessageC2SPacket>>
                        (PacketEvent.Send.class, ChatMessageC2SPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Send<ChatMessageC2SPacket> event)
                    {
                        if (event.getPacket()
                                .chatMessage()
                                .startsWith(Commands.getPrefix()))
                        {
                            applyCommand(event.getPacket().chatMessage());
                            if (!event.getPacket()
                                    .chatMessage()
                                    .toLowerCase()
                                    .startsWith(Commands.getPrefix() + "last ")
                                    && !event.getPacket()
                                    .chatMessage()
                                    .equalsIgnoreCase(Commands.getPrefix()
                                            + "last"))
                            {
                                lastMessage = event.getPacket().chatMessage();
                            }

                            event.setCancelled(true);
                        }
                    }
                });
    }

    public void init()
    {
        Earthhack.getLogger().info("Initializing Commands.");

        // Initialize commands here.

        setupAndConcatenate();
    }

    @Override
    public void register(Command command) throws AlreadyRegisteredException
    {
        if (command.isHidden())
        {
            hidden.add(command);
        }
        else
        {
            commands.add(command);
        }

        if (command instanceof Registrable)
        {
            ((Registrable) command).onRegister();
        }

        setupAndConcatenate();
    }

    @Override
    public void unregister(Command command) throws CantUnregisterException
    {
        if (command instanceof Registrable)
        {
            ((Registrable) command).onUnRegister();
        }

        hidden.remove(command);
        commands.remove(command);
        setupAndConcatenate();
    }

    @Override
    public Command getObject(String name)
    {
        Command command = CommandUtil.getNameableStartingWith(name, commands);
        if (command == null || !command.getName().equalsIgnoreCase(name))
        {
            command = CommandUtil.getNameableStartingWith(name, hidden);
            if (command != null && !command.getName().equalsIgnoreCase(name))
            {
                return null;
            }
        }

        return command;
    }

    @Override
    public <C extends Command> C getByClass(Class<C> clazz)
    {
        C command = CollectionUtil.getByClass(clazz, commands);
        if (command == null)
        {
            command = CollectionUtil.getByClass(clazz, hidden);
        }

        return command;
    }

    @Override
    public Collection<Command> getRegistered()
    {
        return commands;
    }

    public String getLastCommand()
    {
        return lastMessage;
    }

    /**
     * Ensures that the last command is always
     * the {@link ModuleCommand} and reConcatenates
     * for {@link CommandManager#getConcatenatedCommands()}.
     */
    private void setupAndConcatenate()
    {
        commands.remove(MODULE_COMMAND);
        commands.add(MODULE_COMMAND);
        concatenated = concatenateCommands();
    }

    public void renderCommandGui(String message, int x, int y)
    {
        if (message != null
                && message.startsWith(Commands.getPrefix()))
        {
            DrawContext CONTEXT = new DrawContext(mc, mc.getBufferBuilders().getEffectVertexConsumers());
            String[] array = createArray(message);
            String possible = getCommandForMessage(array)
                    .getPossibleInputs(array)
                    .getFullText();

            int width = x + mc.textRenderer.getWidth(message.trim());
            CONTEXT.drawText(mc.textRenderer, possible, width, y, 0xffffffff, true);
        }
    }

    public boolean onTabComplete(TextFieldWidget inputField)
    {
        if (inputField.getText().startsWith(Commands.getPrefix()))
        {
            String[] array = createArray(inputField.getText());
            Completer completer = getCommandForMessage(array)
                    .onTabComplete(new Completer(inputField.getText(), array));

            inputField.setText(completer.getResult());
            return completer.shouldMcComplete();
        }

        return true;
    }

    // TODO: WHY REQUIRE PREFIX HERE I'M SO RETARDED WTF
    public void applyCommand(String message)
    {
        if (message != null && message.length() > 1)
        {
            applyCommandNoPrefix(removePrefix(message));
        }
    }

    public void applyCommandNoPrefix(String message)
    {
        if (message != null && message.length() > 1)
        {
            // String[] commandSplit = message.split(";"); TODO this
            // for (String s : commandSplit)
            String[] array = createArrayNoPrefix(message);
            executeArgs(array);
        }
    }

    public void executeArgs(String... args)
    {
        Command command = getCommandForMessage(args);
        if (command.equals(FAIL_COMMAND))
        {
            command = getHiddenCommand(args);
        }

        command.execute(args);
    }

    public String getConcatenatedCommands()
    {
        return concatenated;
    }

    public Command getCommandForMessage(String[] array)
    {
        if (array == null || array.length == 0)
        {
            return FAIL_COMMAND;
        }

        for (Command command : commands)
        {
            if (command.fits(array))
            {
                return command;
            }
        }

        return FAIL_COMMAND;
    }

    public String[] createArray(String message)
    {
        String noPrefix = removePrefix(message);
        return CommandUtil.toArgs(noPrefix);
    }

    public String removePrefix(String message) {
        return message.substring(Commands.getPrefix().length());
    }

    public String[] createArrayNoPrefix(String message)
    {
        return CommandUtil.toArgs(message);
    }

    private Command getHiddenCommand(String[] array)
    {
        for (Command command : hidden)
        {
            if (command.fits(array))
            {
                return command;
            }
        }

        return FAIL_COMMAND;
    }

    private String concatenateCommands()
    {
        StringBuilder builder = new StringBuilder();

        Iterator<Command> itr = commands.iterator();
        while (itr.hasNext())
        {
            builder.append(itr.next().getName().toLowerCase());
            if (itr.hasNext())
            {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

}