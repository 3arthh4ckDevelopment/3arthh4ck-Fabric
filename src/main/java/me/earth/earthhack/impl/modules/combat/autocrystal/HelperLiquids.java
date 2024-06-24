package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PlaceData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.List;

/**
 * Helper class for AutoCrystal calculations regarding liquids.
 */
public class HelperLiquids implements Globals
{
    private final AutoCrystal module;

    public HelperLiquids(AutoCrystal module) {
        this.module = module;
    }

    public PlaceData calculate(HelperPlace placeHelper,
                               PlaceData placeData,
                               List<PlayerEntity> friends,
                               List<PlayerEntity> players,
                               float minDamage)
    {
        PlaceData newData = new PlaceData(minDamage);
        newData.setTarget(placeData.getTarget());
        for (PositionData data : placeData.getLiquid())
        {
            if (placeHelper.validate(placeData, data, friends) != null)
            {
                placeHelper.calcPositionData(newData, data, players);
            }
        }

        return newData;
    }

    public Direction getAbsorbFacing(BlockPos pos,
                                      List<Entity> entities,
                                      BlockView access,
                                      double placeRange)
    {
        for (Direction facing : Direction.values())
        {
            if (facing == Direction.DOWN)
            {
                continue;
            }

            BlockPos offset = pos.offset(facing);
            if (BlockUtil.getDistanceSq(offset) >= MathUtil.square(placeRange))
            {
                continue;
            }

            if (access.getBlockState(offset).isReplaceable())
            {
                boolean found = false;
                Box bb = new Box(offset);
                for (Entity entity : entities)
                {
                    if (entity == null
                        || EntityUtil.isDead(entity)
                        /*|| !entity.preventEntitySpawning*/)
                    {
                        continue;
                    }

                    if (module.bbBlockingHelper.blocksBlock(bb, entity))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    return facing;
                }
            }
        }

        return null;
    }
}
