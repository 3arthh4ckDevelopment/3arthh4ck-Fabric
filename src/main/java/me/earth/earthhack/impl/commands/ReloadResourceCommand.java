package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;

public class ReloadResourceCommand extends Command
{
    public ReloadResourceCommand()
    {
        super(new String[][]{{"reloadresources"}});
    }

    @Override
    public void execute(String[] args)
    {
        Managers.FILES.init();
        Managers.CHAT.sendDeleteMessage("Reloaded resources", "", ChatIDs.COMMAND);
    }

}
