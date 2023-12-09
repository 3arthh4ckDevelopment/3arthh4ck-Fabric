package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class RightClickItemEvent extends Event
{
    private final PlayerEntity player;
    private final World worldIn;
    private final Hand hand;

    public RightClickItemEvent(PlayerEntity player,
                               World worldIn,
                               Hand hand)
    {
        this.player = player;
        this.worldIn = worldIn;
        this.hand = hand;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    public World getWorldIn()
    {
        return worldIn;
    }

    public Hand getHand()
    {
        return hand;
    }
}
