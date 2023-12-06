package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class FolderCommand extends Command
{
    public FolderCommand()
    {
        super(new String[][]{{"folder"}});
        CommandDescriptions.register(this, "Opens the 3arthh4ck folder.");
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            //TODO: fix
            Desktop.getDesktop().open(Paths.get("earthhack").toFile());
        }
        catch (IOException e)
        {
            ChatUtil.sendMessage(TextColor.RED + "An error occurred.");
            e.printStackTrace();
        }
    }

}