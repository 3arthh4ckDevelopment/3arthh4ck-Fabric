package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class DumpStackCommand extends Command implements Globals
{
    public DumpStackCommand()
    {
        super(new String[][]{{"dumpStack"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.DARK_RED +
                                 "You must be ingame to use this command.");
            return;
        }

        ItemStack stack = mc.player.getInventory().getStack(
            mc.player.getInventory().selectedSlot);
        ChatUtil.sendMessage(stack.getName());
        NbtCompound nbtTagCompound = new NbtCompound();
        stack.writeNbt(nbtTagCompound);
        ChatUtil.sendMessage(nbtTagCompound.toString());
    }

}
