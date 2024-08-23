package me.earth.earthhack.impl.commands.abstracts;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.YesNoNonPausing;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.client.PlayerManager;
import me.earth.earthhack.impl.managers.thread.lookup.LookUp;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Iterator;

/**
 * A command to call methods of {@link PlayerManager}s.
 */
public abstract class AbstractPlayerManagerCommand extends Command
        implements Globals
{
    private final PlayerManager manager;
    private final String listingName;
    private final String added;
    private final String verb;
    private final String color;

    /**
     * Constructs a new PlayerManagerCommand.
     *
     * @param manager the manager to use.
     * @param name the name of the command
     * @param listingName example: "ModuleSorting", "Friends", or "Enemies".
     * @param verb example: "friended", "enemied"
     * @param added completes "added as" ... example:
     *              "an enemy" or "a friend".
     */
    public AbstractPlayerManagerCommand(PlayerManager manager,
                                        String name,
                                        String listingName,
                                        String verb,
                                        String added,
                                        String color)
    {
        super(new String[][]{{name}, {"add", "del", "list"}, {"name"}});
        this.added = added;
        this.manager = manager;
        this.listingName = listingName;
        this.verb = verb;
        this.color = color;
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length == 1 || args.length == 2
                                    && args[1].equalsIgnoreCase("list"))
        {
            Managers.CHAT.sendDeleteComponent(getComponent(), verb,
                    ChatIDs.PLAYER_COMMAND);
        }
        else if (args.length == 2)
        {
            boolean isAdded = manager.contains(args[1]);
            ChatUtil.sendMessage(args[1]
                    + (isAdded ? TextColor.GREEN : TextColor.RED)
                    + " is "
                    + (isAdded ? "" : "not ")
                    + verb + ".", getName());
        }
        else
        {
            String name = args[2];
            if (args[1].equalsIgnoreCase("add"))
            {
                Managers.LOOK_UP.doLookUp(new LookUp(LookUp.Type.UUID, name)
                {
                    @Override
                    public void onSuccess()
                    {
                        manager.add(name, uuid);
                        Managers.CHAT.sendDeleteMessageScheduled(
                                color
                                    + name
                                    + TextColor.GREEN
                                    + " was added as " + added + ".",
                                name);
                    }

                    @Override
                    public void onFailure()
                    {
                        ChatUtil.sendMessageScheduled(
                                TextColor.RED
                                        + "Failed to find "
                                        + name, getName());
                    }
                });
            }
            else if (args[1].equalsIgnoreCase("del"))
            {
                manager.remove(name);
                Managers.CHAT.sendDeleteMessage(
                        TextColor.RED
                                + name
                                + " un" + verb + ".",
                        name,
                        ChatIDs.PLAYER_COMMAND);
            }
            else
            {
                ChatUtil.sendMessage(
                        TextColor.RED
                                + "Please specify <add/del>.", getName());
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);

        if (args.length == 1)
        {
            return inputs;
        }
        else if (args.length == 2)
        {
            String filler = fillArgs(args[1]);
            if (filler != null)
            {
                return inputs.setCompletion(TextUtil.substring(
                                                            filler,
                                                            args[1].length()))
                             .setRest(filler.equalsIgnoreCase("list")
                                        ? ""
                                        : inputs.getRest());
            }
            else
            {
                String next = LookUpUtil.findNextPlayerName(args[1]);
                return inputs.setCompletion(TextUtil.substring(
                                                next == null ? "" : next,
                                                args[1].length()));
            }
        }
        else if (args.length == 3)
        {
            String next = LookUpUtil.findNextPlayerName(args[2]);
            return inputs.setCompletion(TextUtil.substring(
                                                next == null ? "" : next,
                                                args[2].length()));
        }

        return inputs.setCompletion("").setRest(TextColor.RED + "invalid.");
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        if (completer.isSame())
        {
            if (completer.getArgs().length == 2)
            {
                for (int i = 0; i < getUsage()[0].length; i++)
                {
                    String str = getUsage()[0][i];
                    if (str.equalsIgnoreCase(completer.getArgs()[1]))
                    {
                        String result = i == getUsage()[0].length - 1
                                ? getUsage()[0][0]
                                : getUsage()[0][i + 1];

                        String newInitial =
                                TextUtil.substring(
                                        completer.getInitial().trim(),
                                        0,
                                        completer.getInitial().length()
                                                - completer.getArgs()
                                                [completer.getArgs().length - 1]
                                                .length());

                        completer.setResult(newInitial + result);
                    }
                }
            }
            else
            {
                completer.setMcComplete(true);
            }

            completer.setLastCompleted(completer.getResult());
            return completer;
        }

        String[] args = completer.getArgs();
        if (args.length == 3 && (args[1].equalsIgnoreCase("add")
                                    || args[1].equalsIgnoreCase("del")))
        {
            String next = LookUpUtil.findNextPlayerName(args[2]);
            if (next != null)
            {
                String result = TextUtil.substring(
                        completer.getInitial().trim(),
                        0,
                        completer.getInitial().trim().length()
                            - args[2].length());

                completer.setResult(result + next);
                return completer;
            }
        }

        return super.onTabComplete(completer);
    }

    private String fillArgs(String input)
    {
        for (String str : getUsage()[1])
        {
            if (str.startsWith(input.toLowerCase()))
            {
                return str;
            }
        }

        return null;
    }

    private MutableText getComponent()
    {
        MutableText component = Text.empty().append(listingName + ": ");
        Iterator<String> players = manager.getPlayers().iterator();
        while (players.hasNext())
        {
            String name = players.next();
            component.append(
                    Text.empty().append(color
                            + name
                            + TextColor.WHITE
                            + (players.hasNext()
                                ? ", "
                                : ""))
                    .setStyle(Style.EMPTY
                            .withHoverEvent(ChatComponentUtil.setOffset(
                                new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                        Text.empty().append("UUID: " +
                                            manager.getPlayersWithUUID()
                                                    .get(name)))))
                            .withClickEvent(new RunnableClickEvent(() ->
                            {
                                Screen before = mc.currentScreen;
                                mc.setScreen(new YesNoNonPausing(
                                        (result) ->
                                    {
                                        mc.setScreen(before);
                                        if (!result)
                                        {
                                            return;
                                        }

                                        manager.remove(name);
                                        Managers.CHAT.sendDeleteComponent(
                                                getComponent(),
                                                verb,
                                                ChatIDs.PLAYER_COMMAND);

                                        ChatUtil.sendMessage(TextColor.RED
                                                                + name
                                                                + " un"
                                                                + verb
                                                                + ".", getName());
                                    },
                                        Text.empty().append(""),
                                        Text.empty().append(color
                                        + name
                                        + TextColor.WHITE
                                        + " " +
                                        "will be un"
                                        + verb
                                        + ". Continue?")));
                            }))));
        }

        return component;
    }

}
