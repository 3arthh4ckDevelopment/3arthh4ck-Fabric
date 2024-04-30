package me.earth.earthhack.impl.modules.combat.anvilaura.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AnvilResult implements Globals, Comparable<AnvilResult>
{
    // TODO: Smart AnvilBB!
    private static final Box ANVIL_BB =
            new Box(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

    private final Set<BlockPos> positions;
    private final Set<BlockPos> mine;
    private final Set<BlockPos> trap;
    private final PlayerEntity player;
    private final BlockPos playerPos;
    private final BlockPos pressurePos;
    private final boolean validPressure;
    private final boolean fallingEntities;
    private final boolean specialPressure;

    public AnvilResult(Set<BlockPos> positions,
                       Set<BlockPos> mine,
                       Set<BlockPos> trap,
                       PlayerEntity player,
                       BlockPos playerPos,
                       BlockPos pressurePos,
                       boolean validPressure,
                       boolean fallingEntities,
                       boolean specialPressure)
    {
        this.positions = positions;
        this.mine = mine;
        this.trap = trap;
        this.player = player;
        this.playerPos = playerPos;
        this.pressurePos = pressurePos;
        this.validPressure = validPressure;
        this.fallingEntities = fallingEntities;
        this.specialPressure = specialPressure;
    }

    public PlayerEntity getPlayer()
    {
        return player;
    }

    public BlockPos getPressurePos()
    {
        return pressurePos;
    }

    public BlockPos getPlayerPos()
    {
        return playerPos;
    }

    public Set<BlockPos> getPositions()
    {
        return positions;
    }

    public Set<BlockPos> getMine()
    {
        return mine;
    }

    public Set<BlockPos> getTrap()
    {
        return trap;
    }

    public boolean hasValidPressure()
    {
        return validPressure;
    }

    public boolean hasFallingEntities()
    {
        return fallingEntities;
    }

    public boolean hasSpecialPressure()
    {
        return specialPressure;
    }

    @Override
    public int hashCode()
    {
        return player.getId() * 31 + playerPos.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof AnvilResult)
        {
            return ((AnvilResult) obj).player.equals(this.player)
                    && ((AnvilResult) obj).playerPos.equals(this.playerPos);
        }

        return false;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public int compareTo(AnvilResult o)
    {
        if (this.equals(o))
        {
            return 0;
        }

        int r = Double.compare(BlockUtil.getDistanceSq(o.playerPos),
                               BlockUtil.getDistanceSq(this.playerPos));
        return r == 0 ? 1 : r;
    }

    public static Set<AnvilResult> create(List<AbstractClientPlayerEntity> players,
                                          List<Entity> entities,
                                          double minY,
                                          double range)
    {
        Set<AnvilResult> results = new TreeSet<>();
        PlayerEntity rotation = RotationUtil.getRotationPlayer();
        for (PlayerEntity player : players)
        {
            if (player.getY() < 0
                || EntityUtil.isDead(player)
                || player.equals(RotationUtil.getRotationPlayer())
                || player.equals(mc.player)
                || Managers.FRIENDS.contains(player))
            {
                continue;
            }

            double distance = MathUtil.square(player.getX() - rotation.getX())
                            + MathUtil.square(player.getZ() - rotation.getZ());

            if (distance > MathUtil.square(range))
            {
                continue;
            }

            for (BlockPos pos : PositionUtil.getBlockedPositions(
                    player.getBoundingBox(), 1.0))
            {
                if (player.getBoundingBox()
                          .intersects(ANVIL_BB.offset(pos)))
                {
                    checkPos(player, pos, results, entities, minY, range);
                }
            }
        }

        return results;
    }

    private static void checkPos(PlayerEntity player,
                                 BlockPos playerPos,
                                 Set<AnvilResult> results,
                                 List<Entity> entities,
                                 double minY,
                                 double range)
    {
        int x = playerPos.getX();
        int z = playerPos.getZ();

        BlockPos upUp = playerPos.up(2);
        Set<BlockPos> trap = new LinkedHashSet<>();
        for (Direction facing : Direction.HORIZONTAL)
        {
            BlockPos trapPos = upUp.offset(facing);
            if (ObbyModule.HELPER.getBlockState(trapPos)
                                 .isReplaceable())
            {
                trap.add(trapPos);
            }
        }

        boolean validPressure = true;
        BlockPos pressure = playerPos;
        boolean specialPressure = false;
        Set<BlockPos> mine = new LinkedHashSet<>();
        BlockState playerState = ObbyModule.HELPER.getBlockState(pressure);
        if (!playerState.isReplaceable()
                && !SpecialBlocks.PRESSURE_PLATES
                                 .contains(playerState.getBlock()))
        {
            if (playerState.getBlock() == Blocks.ANVIL)
            {
                validPressure = false;
                mine.add(pressure);
            }
            else if (!mc.world.canPlace( // <--- this might be wrong!
                        playerState, pressure, ShapeContext.of(player))
                && playerState.getCollisionShape(
                        ObbyModule.HELPER, pressure).getBoundingBox().maxY < 1.0)
            {
                // this means the Anvil will break when it falls on this block.
                specialPressure = true;
            }

            pressure = playerPos.up();
            playerState = ObbyModule.HELPER.getBlockState(pressure);
            if (!playerState.isReplaceable())
            {
                if (playerState.getBlock() == Blocks.ANVIL)
                {
                    mine.add(pressure);
                }
                else
                {
                    return;
                }
            }
        }

        if (validPressure && !specialPressure)
        {
            BlockPos pressureDown = pressure.down();
            BlockState state = ObbyModule.HELPER.getBlockState(pressureDown);
            if (!isTopSolid(pressureDown, state.getBlock(), state,
                            Direction.UP, ObbyModule.HELPER)
                    && !(state.getBlock() instanceof FenceBlock))
            {
                validPressure = false;
            }
        }

        BlockPos lowest = null;
        boolean fallingEntities = false;
        double yPos = RotationUtil.getRotationPlayer().getY();
        Set<BlockPos> positions = new LinkedHashSet<>();
        for (double y = yPos - range; y < yPos + range; y++)
        {
            BlockPos pos = new BlockPos(x, (int) y, z);
            fallingEntities = fallingEntities || checkForFalling(pos, entities);
            if (y < player.getY() + minY)
            {
                continue;
            }


            if (!FallingBlock.canFallThrough(
                    ObbyModule.HELPER.getBlockState(pos)))
            {
                break;
            }

            if (lowest == null)
            {
                lowest = pos;
            }

            positions.add(pos);
        }

        if (lowest == null)
        {
            return;
        }

        boolean bad = false;
        for (int y = pressure.getY(); y < lowest.getY(); y++)
        {
            BlockPos pos = new BlockPos(x, y, z);
            fallingEntities = fallingEntities || checkForFalling(pos, entities);
            if (pos.getY() == pressure.getY())
            {
                continue;
            }

            BlockState state = ObbyModule.HELPER.getBlockState(pos);
            if (!FallingBlock.canFallThrough(state))
            {
                if (state.getBlock() == Blocks.ANVIL)
                {
                    mine.add(pos);
                    continue;
                }

                bad = true;
                break;
            }
        }

        if (bad)
        {
            return;
        }

        results.add(new AnvilResult(positions, mine, trap, player, playerPos,
                                    pressure, validPressure, fallingEntities,
                                    specialPressure));
    }

    private static boolean checkForFalling(BlockPos pos, List<Entity> entities)
    {
        Box bb = new Box(pos);
        for (Entity entity : entities)
        {
            if (entity instanceof FallingBlockEntity
                    && entity.isAlive()
                    && entity.getBoundingBox().intersects(bb))
            {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({"deprecation", "SameParameterValue"})
    private static boolean isTopSolid(BlockPos pos, Block block,
                                      BlockState base_state,
                                      Direction side,
                                      BlockView world)
    {
        if (base_state.isSideSolid(world, pos, Direction.UP, SideShapeType.FULL)
                && side == Direction.UP) // Short circuit to vanilla function if it's true
            return true;

        if (block instanceof SlabBlock)
        {
            BlockState state = block.getStateWithProperties(base_state);
            return base_state.isFullCube(world, pos)
                || (state.get(SlabBlock.TYPE)
                        == SlabType.TOP && side == Direction.UP)
                || (state.get(SlabBlock.TYPE) == SlabType.BOTTOM
                        && side == Direction.DOWN);
        }
        else if (block instanceof FarmlandBlock)
        {
            return (side != Direction.DOWN && side != Direction.UP);
        }
        else if (block instanceof StairsBlock)
        {
            BlockState state = block.getStateWithProperties(base_state);
            boolean flipped = state.get(StairsBlock.HALF) == BlockHalf.TOP;
            StairShape shape = state.get(StairsBlock.SHAPE);
            Direction facing = state.get(StairsBlock.FACING);
            if (side == Direction.UP) return flipped;
            if (side == Direction.DOWN) return !flipped;
            if (facing == side) return true;
            if (flipped)
            {
                if (shape == StairShape.INNER_LEFT ) return side == facing.rotateYCounterclockwise();
                if (shape == StairShape.INNER_RIGHT) return side == facing.rotateYClockwise();
            }
            else
            {
                if (shape == StairShape.INNER_LEFT ) return side == facing.rotateYClockwise();
                if (shape == StairShape.INNER_RIGHT) return side == facing.rotateYCounterclockwise();
            }
            return false;
        }
        else if (block instanceof SnowBlock)
        {
            BlockState state = block.getStateWithProperties(base_state);
            return state.get(SnowBlock.LAYERS) >= 8;
        }
        else if (block instanceof HopperBlock && side == Direction.UP)
        {
            return true;
        }
        // else if (block instanceof CompressedPoweredBlock)
        // {
        //     return true;
        // }

        return base_state.isSolid();
    }

}
