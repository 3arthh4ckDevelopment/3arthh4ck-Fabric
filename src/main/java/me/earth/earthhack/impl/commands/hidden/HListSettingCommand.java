package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandScheduler;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.client.gui.screen.ChatScreen;

//TODO: pages when too many settings (With +- component)?
//TODO: maybe a ChatScreen Handler, to update ChatLines etc?
public class HListSettingCommand extends Command
        implements Globals, CommandScheduler
{
    public HListSettingCommand()
    {
        super(new String[][]{{"hiddenlistsetting"}, {"module"}}, true);
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 1)
        {
            Module module = Managers.MODULES.getObject(args[1]);
            if (module != null)
            {
                sendSettings(module);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        return PossibleInputs.empty();
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        completer.setMcComplete(true);
        return completer;
    }

    //TODO: find out how much we gotta scroll?
    private static void sendSettings(Module module)
    {
        Managers.CHAT.sendDeleteMessage(" ",
                module.getName() + "1",
                ChatIDs.CHAT_GUI);
        // todo: style accesswidener + chatcomponents
        /*
        MutableText delComp = Text.empty();
        Managers.CHAT.sendDeleteComponent(
                delComp.append(module.getName()
                        + " : "
                        + TextColor.GRAY
                        + module.getCategory().toString())
                        .setStyle(new Style(0xffffff).setHoverEvent(
                                ChatComponentUtil.setOffset(
                                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.of(
                                                        module.getData().getDescription())) {
                                                })))),
                module.getName() + "2",
                ChatIDs.CHAT_GUI;

        for (Setting<?> setting : module.getSettings())
        {
            if (SettingsModule.shouldDisplay(setting))
            {
                MutableText component = ComponentFactory.create(setting);
                Managers.CHAT.sendDeleteComponent(
                        component,
                        setting.getName()
                                + module.getName(),
                        ChatIDs.CHAT_GUI);
            }
        }
        */
        Managers.CHAT.sendDeleteMessage(" ",
                module.getName() + "3",
                ChatIDs.CHAT_GUI);

        Scheduler.getInstance().schedule(() ->
                mc.setScreen(new ChatScreen("")));

        SCHEDULER.submit(() -> mc.execute(() ->
        {
            if (mc.inGameHud != null)
            {
                mc.inGameHud.getChatHud().scroll(1);
            }
        }), 100);
    }

    public static String create(Module module)
    {
        return Commands.getPrefix()
                + "hiddenlistsetting "
                + module.getName();
    }

}
