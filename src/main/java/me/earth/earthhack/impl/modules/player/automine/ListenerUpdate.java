package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.modules.player.automine.util.*;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.WorldUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

final class ListenerUpdate extends ModuleListener<AutoMine, UpdateEvent>
{
    private static final ModuleCache<Speedmine> SPEED_MINE =
        Caches.getModule(Speedmine.class);
    // private static final ModuleCache<AnvilAura> ANVIL_AURA =
    //     Caches.getModule(AnvilAura.class);
    private static final ModuleCache<AntiSurround> ANTISURROUND =
        Caches.getModule(AntiSurround.class);
    private static final ModuleCache<Surround> SURROUND =
        Caches.getModule(Surround.class);
    private static final ModuleCache<Step> STEP =
        Caches.getModule(Step.class);

    private Set<BlockPos> surrounding = Collections.emptySet();

    public ListenerUpdate(AutoMine module)
    {
        super(module, UpdateEvent.class, 1);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        surrounding = Collections.emptySet();
        if (ANTISURROUND.returnIfPresent(AntiSurround::isActive, false)
            /*|| ANVIL_AURA.isEnabled() && ANVIL_AURA.get().isMining()*/)
        {
            return;
        }

        if (!SPEED_MINE.isPresent())
        {
            ModuleUtil.disable(module, TextColor.RED
                + "Disabled, Speedmine isn't registered on"
                + " this version of the client!");
            return;
        }

        if ((module.mode.getValue() == AutoMineMode.Combat
            || module.mode.getValue() == AutoMineMode.AntiTrap)
            && (!SPEED_MINE.isEnabled()
            || !(SPEED_MINE.get().getMode() == MineMode.Smart
                || SPEED_MINE.get().getMode() == MineMode.Fast
                || SPEED_MINE.get().getMode() == MineMode.Instant)))
        {
            if (module.disableOnNoSpeedmine.getValue())
            {
                ModuleUtil.disable(module, TextColor.RED
                 + "Disabled, enable Speedmine - Smart for AutoMine - Combat!");
            }

            return;
        }

        if (mc.player.isCreative()
            || mc.player.isSpectator()
            || !module.timer.passed(module.delay.getValue())
            || (module.mode.getValue() == AutoMineMode.Combat
            && SPEED_MINE.get().getPos() != null
            && (module.current == null
            || !module.current.equals(SPEED_MINE.get().getPos()))))
        {
            return;
        }

        BlockPos invalid = null;
        if (module.constellation != null)
        {
            module.constellation.update(module);
        }

        if (module.constellationCheck.getValue()
            && module.constellation != null)
        {
            if (module.constellation.isValid(mc.world,
                                             module.checkPlayerState.getValue())
                && !module.constellationTimer.passed(module.maxTime.getValue())
                && module.constellation.cantBeImproved())
            {
                return;
            }

            if (module.constellation.cantBeImproved())
            {
                invalid = module.current;
                module.constellation = null;
                module.current = null;
            }
        }

        if (!module.improve.getValue()
            && module.constellation != null
            && (!module.improveInvalid.getValue()
            || module.constellation.isValid(mc.world, module.checkPlayerState.getValue()))
            && module.constellation.cantBeImproved())
        {
            return;
        }

        module.blackList.entrySet().removeIf(e ->
                                                 (System.currentTimeMillis() - e.getValue()) / 1000.0f
                                                     > module.blackListFor.getValue());

        if (module.mode.getValue() == AutoMineMode.Combat || module.mode.getValue() == AutoMineMode.Compatibility)
        {
            if (module.noSelfMine.getValue() && SURROUND.isPresent())
            {
                surrounding = SURROUND.get().createSurrounding(SURROUND.get().createBlocked(),
                                                               Managers.ENTITIES.getPlayers());
            }

            if (module.prioSelf.getValue()
                && (!module.prioSelfWithStep.getValue() || STEP.isEnabled())
                && checkSelfTrap()
                || checkEnemies(false))
            {
                return;
            }

            BlockPos position = PositionUtil.getPosition();
            if (module.self.getValue()
                && ((!module.prioSelf.getValue()
                    || module.prioSelfWithStep.getValue() && !STEP.isEnabled())
                && checkSelfTrap()
                || checkPos(mc.player, position)))
            {
                return;
            }

            if (module.mineBurrow.getValue() && checkEnemies(true))
            {
                return;
            }

            BlockState state;
            if (module.selfEchestMine.getValue()
                && module.isValid(Blocks.ENDER_CHEST.getDefaultState())
                && (state = mc.world.getBlockState(position))
                .getBlock() == Blocks.ENDER_CHEST)
            {
                attackPos(position,
                          new Constellation(mc.world,
                                            mc.player,
                                            position,
                                            position,
                                            state,
                                            module));
                return;
            }

            if (invalid != null
                && invalid.equals(SPEED_MINE.get().getPos())
                && module.resetIfNotValid.getValue())
            {
                SPEED_MINE.get().reset();
            }

            if (module.constellation == null
                && module.echest.getValue())
            {
                BlockEntity closest = null;
                double minDist = Double.MAX_VALUE;
                for (BlockEntity entity : WorldUtil.getBlockEntities())
                {
                    if (entity instanceof EnderChestBlockEntity
                        && BlockUtil.getDistanceSq(entity.getPos())
                        < MathUtil.square(module.echestRange.getValue()))
                    {
                        double dist = entity.getPos().getSquaredDistanceFromCenter(
                            RotationUtil.getRotationPlayer().getX(),
                            RotationUtil.getRotationPlayer().getY()
                                + mc.player.getEyeHeight(mc.player.getPose()),
                            RotationUtil.getRotationPlayer().getZ());

                        if (dist < minDist)
                        {
                            minDist = dist;
                            closest = entity;
                        }
                    }
                }

                if (closest != null)
                {
                    module.offer(new EchestConstellation(closest.getPos()));
                    module.attackPos(closest.getPos());
                    return;
                }
            }

            if (module.constellation == null
                    && module.shulkers.getValue())
            {
                BlockEntity closest = null;
                double minDist = Double.MAX_VALUE;
                for (BlockEntity entity : WorldUtil.getBlockEntities())
                {
                    if (entity instanceof ShulkerBoxBlockEntity
                            && BlockUtil.getDistanceSq(entity.getPos())
                            < MathUtil.square(module.shulkersRange.getValue()))
                    {
                        double dist = entity.getPos().getSquaredDistanceFromCenter(
                                RotationUtil.getRotationPlayer().getX(),
                                RotationUtil.getRotationPlayer().getY()
                                        + mc.player.getEyeHeight(mc.player.getPose()),
                                RotationUtil.getRotationPlayer().getZ());

                        if (dist < minDist)
                        {
                            minDist = dist;
                            closest = entity;
                        }
                    }
                }

                if (closest != null)
                {
                    module.offer(new ShulkerConstellation(closest.getPos()));
                    module.attackPos(closest.getPos());
                    return;
                }
            }


            if ((module.constellation == null
                || !module.constellation.cantBeImproved()
                && !(module.constellation instanceof BigConstellation))
                && module.terrain.getValue()
                && module.terrainTimer.passed(module.terrainDelay.getValue())
                && module.future == null
                && (!module.checkCrystalDownTime.getValue()
                || module.downTimer.passed(module.downTime.getValue())))
            {
                boolean c = module.closestPlayer.getValue();
                double closest = Double.MAX_VALUE;
                PlayerEntity best = null;
                List<PlayerEntity> players = new ArrayList<>(c ? 0 : 10);
                for (PlayerEntity p : mc.world.getPlayers())
                {
                    if (p == null
                        || EntityUtil.isDead(p)
                        || p.squaredDistanceTo(RotationUtil.getRotationPlayer())
                        > 400
                        || Managers.FRIENDS.contains(p))
                    {
                        continue;
                    }

                    if (c)
                    {
                        double dist =
                            p.squaredDistanceTo(RotationUtil.getRotationPlayer());
                        if (dist < closest)
                        {
                            closest = dist;
                            best = p;
                        }
                    }
                    else
                    {
                        players.add(p);
                    }
                }

                if (c && best == null || !c && players.isEmpty())
                {
                    return;
                }

                List<Entity> entities = new ArrayList<>();
                mc.world.getEntities().forEach(entities::add);

                entities = entities
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(e -> !(e instanceof ItemEntity)) // we ignore items
                    .filter(e -> !EntityUtil.isDead(e)) // to ignore dead entities
                    .filter(e -> !(e.getY() > module.maxY.getValue())) // to ignore entities above a certain Y level
                    .filter(e ->
                                e.squaredDistanceTo(RotationUtil.getRotationPlayer())
                                    < MathUtil.square(module.range.getValue()))
                    .collect(Collectors.toList());

                AutoMineCalc calc = new AutoMineCalc(
                    module,
                    players,
                    surrounding,
                    entities,
                    best,
                    module.minDmg.getValue(),
                    module.maxSelfDmg.getValue(),
                    module.range.getValue(),
                    module.obbyPositions.getValue(),
                    module.newV.getValue(),
                    module.newVEntities.getValue(),
                    module.mineObby.getValue(),
                    module.breakTrace.getValue(),
                    module.suicide.getValue());

                module.future = Managers.THREAD.submit(calc);
                module.terrainTimer.reset();
            }
        }
        else if (module.mode.getValue() == AutoMineMode.AntiTrap)
        {
            BlockPos boost = PositionUtil.getPosition().up(2);
            if (!boost.equals(module.last) && !MovementUtil.isMoving())
            {
                SPEED_MINE.get().getTimer().setTime(0);
                module.current = boost;
                mc.interactionManager.attackBlock(boost, Direction.DOWN);
                module.timer.reset();
                module.last = boost;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean checkEnemies(boolean burrow)
    {
        BlockPos closestPos = null;
        Constellation closest = null;
        double distance = Double.MAX_VALUE;
        for (PlayerEntity player : mc.world.getPlayers())
        {
            if (EntityUtil.isValid(player, module.range.getValue() + 1)
                && !player.equals(mc.player))
            {
                BlockPos playerPos = PositionUtil.getPosition(player);
                if (burrow)
                {
                    double dist = mc.player.squaredDistanceTo(new Vec3d(playerPos.getX(), playerPos.getY(), playerPos.getZ()));
                    if (dist >= distance)
                    {
                        continue;
                    }

                    BlockState state;
                    if (!isValid(playerPos,
                                 (state = mc.world.getBlockState(playerPos))))
                    {
                        continue;
                    }

                    closestPos = playerPos;
                    closest = new Constellation(mc.world,
                                                player,
                                                playerPos,
                                                playerPos,
                                                state,
                                                module);
                    closest.setBurrow(true);
                    distance = dist;
                    continue;
                }

                BlockState playerPosState = mc.world.getBlockState(playerPos);
                if (playerPosState.isReplaceable()
                    || playerPosState
                    .getBlock()
                    .getBlastResistance() < 100)
                {
                    // TODO: up in case player phases
                    BlockPos upUp = playerPos.up(2);
                    BlockState headState = mc.world.getBlockState(upUp);
                    if (module.head.getValue() || module.crystal.getValue())
                    {
                        if (module.head.getValue() && isValid(upUp, headState)
                            || module.crystal.getValue()
                            && headState.getBlock() == Blocks.OBSIDIAN
                            && module.isValidCrystalPos(upUp))
                        {
                            attackPos(upUp,
                                      new CrystalConstellation(mc.world,
                                                               player,
                                                               upUp,
                                                               playerPos,
                                                               headState,
                                                               module));
                            return true;
                        }
                    }

                    for (Direction facing : Direction.HORIZONTAL)
                    {
                        BlockPos tempUpUp;
                        BlockState tempHeadState = headState;

                        BlockPos offset = playerPos.offset(facing);
                        BlockState state = mc.world.getBlockState(offset);
                        // in a 2x1 this won't cover these blocks (p)
                        //      p x
                        //    x a a x
                        //      p x
                        // but it's fine, because that's covered by mineL
                        if (state.getBlock() == Blocks.AIR
                            && player.getBoundingBox().intersects(new Box(offset)))
                        {
                            // TODO: we should also take offset.up(1) for crystal kinda
                            tempUpUp  = offset.up(2);
                            tempHeadState = mc.world.getBlockState(tempUpUp);
                            offset = offset.offset(facing);
                            state = mc.world.getBlockState(offset);
                        }

                        double dist = mc.player.squaredDistanceTo(offset.getX(), offset.getY(), offset.getZ());
                        if (dist >= distance)
                        {
                            continue;
                        }

                        boolean valid = isValid(offset, state);
                        if (valid)
                        {
                            if (module.mineL.getValue()
                                && mc.world.getBlockState(offset.up())
                                           .isReplaceable())
                            {
                                boolean found = false;
                                for (Direction l : Direction.HORIZONTAL)
                                {
                                    if (l == facing || l == facing.getOpposite())
                                    {
                                        continue;
                                    }

                                    if (module.checkCrystalPos(offset.offset(l)
                                                                     .down()))
                                    {
                                        closestPos = offset;
                                        closest = new Constellation(mc.world,
                                                                    player,
                                                                    offset,
                                                                    playerPos,
                                                                    state,
                                                                    module);
                                        closest.setL(true);
                                        distance = dist;
                                        found = true;
                                        break;
                                    }
                                }

                                if (found)
                                {
                                    continue;
                                }
                            }

                            BlockPos finalOffset = offset;
                            if (module.checkCrystalPos(offset.offset(facing).down())
                                && (!(module.dependOnSMCheck.getValue() || module.speedmineCrystalDamageCheck.getValue())
                                    || SPEED_MINE.returnIfPresent(sm -> sm.crystalHelper.calcCrystal(finalOffset, player, true), null) != null))
                            {
                                closestPos = offset;
                                closest = new Constellation(mc.world,
                                                            player,
                                                            offset,
                                                            playerPos,
                                                            state,
                                                            module);
                                distance = dist;
                            }
                        }

                        if (module.crystal.getValue() && (valid && module.isValidCrystalPos(offset)
                            || module.isValidCrystalPos((offset = offset.up()))
                            && tempHeadState.getBlock() == Blocks.AIR
                            && isValid(offset, (state = mc.world.getBlockState(offset)))))
                        {
                            closestPos = offset;
                            closest = new CrystalConstellation(mc.world,
                                                               player,
                                                               offset,
                                                               playerPos,
                                                               state,
                                                               module);
                            distance = dist;
                        }
                    }
                }
            }
        }

        if (closest != null)
        {
            attackPos(closestPos, closest);
            return true;
        }

        return false;
    }

    private boolean checkSelfTrap()
    {
        BlockPos playerPos = PositionUtil.getPosition();
        BlockPos upUp = playerPos.up(2);
        BlockState state = mc.world.getBlockState(upUp);
        if (isValid(upUp, state))
        {
            Constellation c = new Constellation(mc.world,
                                                mc.player,
                                                upUp,
                                                playerPos,
                                                state,
                                                module);
            attackPos(upUp, c);
            c.setSelfUntrap(true);
            return true;
        }

        return false;
    }

    private boolean checkPos(PlayerEntity player, BlockPos playerPos)
    {
        for (Direction facing : Direction.HORIZONTAL)
        {
            BlockPos offset = playerPos.offset(facing);
            BlockState state = mc.world.getBlockState(offset);
            if (isValid(offset, state)
                && module.checkCrystalPos(offset.offset(facing).down()))
            {
                attackPos(offset,
                          new Constellation(mc.world,
                                            player,
                                            offset,
                                            playerPos,
                                            state,
                                            module));
                return true;
            }
        }

        return false;
    }

    private boolean isValid(BlockPos pos, BlockState state)
    {
        return !module.blackList.containsKey(pos)
            && !surrounding.contains(pos)
            && MineUtil.canBreak(state, pos)
            && module.isValid(state)
            && mc.player.squaredDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ())) <= MathUtil
            .square(SPEED_MINE
                        .get()
                        .getRange())
            && !state.isReplaceable();
    }

    public void attackPos(BlockPos pos, Constellation c)
    {
        // just so I can test if this is necessary ?
        if (module.checkCurrent.getValue()
            && pos.equals(module.current))
        {
            return;
        }

        module.offer(c);
        module.attackPos(pos);
    }

}
