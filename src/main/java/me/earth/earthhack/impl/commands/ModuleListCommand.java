package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.text.*;

import java.util.Comparator;
import java.util.List;

public class ModuleListCommand extends Command
{
    public ModuleListCommand()
    {
        super(new String[][]{{"modules"}});
        CommandDescriptions.register(this, "List all modules in the client." +
                " Leftclick a module to toggle it. Middleclick a module to" +
                " open the chatgui and get a list of its settings.");
    }

    @Override
    public void execute(String[] args)
    {
        Managers.CHAT.sendDeleteComponent(getComponent(),
                "moduleListCommand",
                ChatIDs.MODULE);
    }

    public static MutableText getComponent()
    {
        AbstractTextComponent component = new SimpleComponent("ModuleSorting: ");
        component.setWrap(true);

        List<Module> moduleList = Managers
                .MODULES
                .getRegistered()
                .stream()
                .sorted(Comparator
                        .comparing(Displayable::getDisplayName))
                .toList();

        for (int i = 0; i < moduleList.size(); i++)
        {
            Module module = moduleList.get(i);
            if (module != null)
            {
                int finalI = i;
                MutableText sibling =
                        new SuppliedComponent(() ->
                                (module.isEnabled()
                                        ? TextColor.GREEN
                                        : TextColor.RED) + module.getName()
                                        + (finalI == moduleList.size() - 1 ? "" : ", "))
                                .setWrap(true);

                Style style = Style.EMPTY
                        .withHoverEvent(
                                ChatComponentUtil.setOffset(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.of(module.getData()
                                                        .getDescription()))))
                        .withClickEvent(new SmartClickEvent(
                                ClickEvent.Action.RUN_COMMAND)
                        {
                            @Override
                            public String getValue()
                            {
                                return Commands.getPrefix()
                                        + "toggle "
                                        + module.getName();
                            }
                        });

                style.withInsertion(Commands.getPrefix()
                                        + module.getName());

                style.withClickEvent(new SmartClickEvent
                        (ClickEvent.Action.RUN_COMMAND)
                {
                    @Override
                    public String getValue()
                    {
                        return Commands.getPrefix()
                                + module.getName();
                    }
                });

                sibling.setStyle(style);
                component.append(sibling);
            }
        }

        return component;
    }

}
