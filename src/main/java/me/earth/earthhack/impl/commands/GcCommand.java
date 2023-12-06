package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;

public class GcCommand extends Command {

    public GcCommand() {
        super(new String[][]{{"gc"}});
    }

    @Override
    public void execute(String[] args)
    {
        System.gc();
    }

}
