package me.earth.earthhack.impl.modules.player.spectate;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

final class ListenerMove extends ModuleListener<Spectate, MoveEvent>
{
    public ListenerMove(Spectate module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.stopMove.getValue())
        {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();

            // If we are standing on a block the event.y is slightly smaller
            // than 0. If the Block was removed, we would fall a bit which
            // this prevents.
            if (y != 0.0)
            {
                for (VoxelShape a :
                        mc.world.getCollisions(
                              mc.player,
                              mc.player.getBoundingBox().expand(x, y, z)))
                {
                    y = a.calculateMaxDistance(Direction.Axis.Y, mc.player.getBoundingBox(), y);
                }
            }

            event.setX(0);
            event.setY(y == 0.0 ? -0.0784000015258789 : 0);
            event.setZ(0);
        }
    }

}
