package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClickBlockEvent extends Event
{
    private final BlockPos pos;
    private final Direction facing;

    public ClickBlockEvent(BlockPos pos, Direction facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public Direction getFacing()
    {
        return facing;
    }

    public static class Right extends ClickBlockEvent
    {
        private final Vec3d vec;
        private final Hand hand;

        public Right(BlockPos pos, Direction facing, Vec3d vec, Hand hand)
        {
            super(pos, facing);
            this.vec = vec;
            this.hand = hand;
        }

        public Hand getHand()
        {
            return hand;
        }

        public Vec3d getVec()
        {
            return vec;
        }
    }
}
