package me.earth.earthhack.impl.event.events.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class EatEvent
{
    private final ItemStack stack;
    private final ClientPlayerEntity entity;

    public EatEvent(ItemStack stack, ClientPlayerEntity entity)
    {
        this.stack = stack;
        this.entity = entity;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public ClientPlayerEntity getEntity()
    {
        return entity;
    }

}
