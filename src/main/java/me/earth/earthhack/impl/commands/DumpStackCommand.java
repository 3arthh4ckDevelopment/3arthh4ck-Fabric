package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
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
                                 "You must be ingame to use this command.", getName());
            return;
        }

        ItemStack stack = mc.player.getInventory().getStack(
            mc.player.getInventory().selectedSlot);
        ChatUtil.sendMessage(stack.getName().getString(), getName());
        NbtComponent nbtComponent = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (nbtComponent == null) {
            ChatUtil.sendMessage(TextColor.DARK_RED + "This item has no Block entity data", getName());
            return;
        }

        NbtCompound nbtTagCompound = nbtComponent.copyNbt();
        Earthhack.getLogger().info(nbtTagCompound.toString());
        ChatUtil.sendMessage(nbtTagCompound.toString(), getName());
    }

}
