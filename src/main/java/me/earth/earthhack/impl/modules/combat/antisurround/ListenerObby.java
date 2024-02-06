package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

final class ListenerObby extends ObbyListener<AntiSurround>
{
    private BlockPos crystalPos = null;

    public ListenerObby(AntiSurround module)
    {
        super(module, EventBus.DEFAULT_PRIORITY);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (!module.async.getValue() && !module.normal.getValue())
        {
            module.reset();
            return;
        }

        crystalPos = null;
        if (module.active.get() || module.semiActive.get())
        {
            synchronized (module)
            {
                module.reset();
            }
        }

        synchronized (module)
        {
            if (module.active.get())
            {
                PlayerEntity target = module.target;
                if (target == null || EntityUtil.isDead(target))
                {
                    module.reset();
                    return;
                }

                BlockState state;
                if (!(state = mc.world
                                .getBlockState(PositionUtil.getPosition(target)))
                                .isReplaceable()
                        && state.getBlock() // burrow
                                .getBlastResistance() > 100)
                {
                    module.reset();
                    return;
                }

                if (module.pos == null)
                {
                    module.reset();
                    return;
                }

                if (module.stopOnObby.getValue()
                    && mc.world.getBlockState(module.pos)
                               .getBlock() == Blocks.OBSIDIAN)
                {
                    module.reset();
                    return;
                }

                IBlockStateHelper helper = new BlockStateHelper();
                helper.addAir(module.pos);
                float damage = DamageUtil.calculate(module.pos, target, (ClientWorld) helper);
                if (damage < module.minDmg.getValue())
                {
                    module.reset();
                    return;
                }
            }
            else if (module.semiActive.get()
                    && System.nanoTime() - module.semiActiveTime
                        > TimeUnit.MILLISECONDS.toNanos(15))
            {
                module.semiActive.set(false);
            }
        }

        if (!module.active.get()
            && event.getStage() == Stage.PRE
            && module.persistent.getValue()
            && !module.holdingCheck())
        {
            MineSlots mine = HelperLiquids.getSlots(module.onGround.getValue());
            if (mine.getBlockSlot() == -1
                || mine.getToolSlot() == -1
                || mine.getDamage() < module.minMine.getValue()
                    && !(module.isAnvil = module.anvilCheck(mine)))
            {
                return;
            }

            int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
            if (crystalSlot == -1)
            {
                return;
            }

            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
            for (PlayerEntity player : mc.world.getPlayers())
            {
                if (player == null
                    || EntityUtil.isDead(player)
                    || player.equals(mc.player)
                    || player.equals(RotationUtil.getRotationPlayer())
                    || Managers.FRIENDS.contains(player)
                    || player.squaredDistanceTo(RotationUtil.getRotationPlayer())
                            > MathUtil.square(module.range.getValue() + 2))
                {
                    continue;
                }

                BlockPos playerPos = PositionUtil.getPosition(player);
                for (Direction facing : Direction.HORIZONTAL)
                {
                    BlockPos pos = playerPos.offset(facing);
                    if (BlockUtil.getDistanceSq(pos)
                        > MathUtil.square(module.range.getValue()))
                    {
                        continue;
                    }

                    BlockPos down = pos.offset(facing).down();
                    if (BlockUtil.getDistanceSq(down)
                        > MathUtil.square(module.range.getValue()))
                    {
                        continue;
                    }

                    Entity blocking = module.getBlockingEntity(
                            pos, Managers.ENTITIES.getEntities());
                    if (blocking != null
                        && !(blocking instanceof EndCrystalEntity))
                    {
                        continue;
                    }

                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isReplaceable()
                        || state.getBlock() == Blocks.BEDROCK
                        || state.getBlock() == Blocks.OBSIDIAN
                        || state.getBlock() == Blocks.ENDER_CHEST)
                    {
                        continue;
                    }

                    int slot = MineUtil.findBestTool(playerPos, state);
                    double damage = MineUtil.getDamage(state,
                        mc.player.getInventory().getStack(slot), playerPos,
                        RotationUtil.getRotationPlayer().isOnGround());

                    if (damage < module.minMine.getValue())
                    {
                        continue;
                    }

                    if (BlockUtil.canPlaceCrystalReplaceable(down,
                            true, module.newVer.getValue(),
                            mc.world.getOtherEntities(null, null),
                            module.newVerEntities.getValue(), 0))
                    {
                        BlockState dState = mc.world.getBlockState(down);
                        if ((!module.obby.getValue() || obbySlot == -1)
                            && dState.getBlock() != Blocks.OBSIDIAN
                            && dState.getBlock() != Blocks.BEDROCK)
                        {
                            continue;
                        }

                        BlockPos on = null;
                        Direction onFacing = null;
                        for (Direction off : Direction.values())
                        {
                            on = pos.offset(off);
                            if (BlockUtil.getDistanceSq(on)
                                <= MathUtil.square(module.range.getValue())
                                    && !mc.world.getBlockState(on)
                                                .isReplaceable())
                            {
                                onFacing = off.getOpposite();
                                break;
                            }
                        }

                        if (onFacing == null) // TODO: helping blocks?
                        {
                            continue;
                        }

                        synchronized (module)
                        {
                            if (!module.isActive())
                            {
                                module.semiPos = null;
                            }

                            if (module.placeSync(pos, down, on, onFacing,
                                obbySlot, mine, crystalSlot, blocking,
                                player, false))
                            {
                                // override toolSlot to best slot for block
                                module.toolSlot = slot;
                                module.mine     = true;
                                if (module.rotations != null
                                    && module.rotate.getValue()
                                        != Rotate.None)
                                {
                                    setRotations(module.rotations, event);
                                }
                                else
                                {
                                    module.execute();
                                }
                            }
                        }

                        return;
                    }
                }
            }
        }

        synchronized (module)
        {
            if (!module.active.get())
            {
                if (module.semiActive.get()
                    && System.nanoTime() - module.semiActiveTime
                        > TimeUnit.MILLISECONDS.toNanos(15))
                {
                    module.semiActive.set(false);
                }

                return;
            }

            if (module.holdingCheck())
            {
                module.reset();
                return;
            }

            super.invoke(event);
        }
    }

    @Override
    protected boolean updatePlaced()
    {
        super.updatePlaced();
        if (module.pos == null || module.crystalPos == null)
        {
            module.reset();
        }

        // dont do anything if module is not active
        return !module.active.get();
    }

    @Override
    protected boolean hasTimerNotPassed()
    {
        boolean result = super.hasTimerNotPassed();
        if (module.isAnvil && module.pos != null)
        {
            if (!module.hasMined)
            {
                mine(module.pos);
                return false;
            }
            else if (++module.ticks < 4)
            {
                return false;
            }

            if (!result)
            {
                return false;
            }
        }

        return result;
    }

    // TODO: put this on execute so we can rotate...
    private void mine(BlockPos pos)
    {
        Direction facing = RayTraceUtil.getFacing(
                RotationUtil.getRotationPlayer(), pos, true);

        PacketUtil.startDigging(pos, facing);
        if (module.digSwing.getValue())
        {
            Swing.Packet.swing(Hand.MAIN_HAND);
        }

        module.hasMined = true;
        module.ticks = 0;
    }

    @Override
    protected int getSlot()
    {
        module.obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        MineSlots slots = HelperLiquids.getSlots(module.onGround.getValue());
        if (slots.getDamage() < module.minMine.getValue()
                && !(module.isAnvil = module.anvilCheck(slots))
            || slots.getToolSlot() == -1
            || slots.getBlockSlot() == -1)
        {
            module.reset();
            return -1;
        }

        module.crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        if (module.crystalSlot == -1)
        {
            module.reset();
            return -1;
        }

        module.toolSlot = slots.getToolSlot();
        return slots.getBlockSlot();
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        BlockPos pos = module.pos;
        BlockPos crystalPos = module.crystalPos;
        if (pos == null || crystalPos == null)
        {
            result.setValid(false);
            return result;
        }

        if (mc.world.getBlockState(pos).isReplaceable())
        {
            AntiSurround.HELPER.addBlockState(pos,
                                              Blocks.AIR.getDefaultState());
            result.getTargets().add(pos);
        }
        else if (entityCheck(pos))
        {
            AntiSurround.HELPER.addBlockState(pos,
                                              Blocks.AIR.getDefaultState());
            module.mine = true;
            result.getTargets().add(pos);
        }
        else
        {
            placeObby(crystalPos, result);
        }

        return result;
    }

    @Override
    protected void disableModule()
    {
        module.reset();
    }

    @Override
    protected boolean rotateCheck()
    {
        if (crystalPos != null
                && (!module.isAnvil || module.ticks > 3 && module.hasMined))
        {
            IBlockStateHelper helper = new BlockStateHelper();
            helper.addBlockState(crystalPos, Blocks.OBSIDIAN.getDefaultState());
            BlockHitResult ray = null;
            if (module.rotations != null)
            {
                // TODO: helper
                ray = RotationUtil.rayTraceWithYP(crystalPos, mc.world,
                        module.rotations[0], module.rotations[1],
                        (b, p) -> p.equals(crystalPos));
            }

            if (ray == null)
            {
                double x = RotationUtil.getRotationPlayer().getX();
                double y = RotationUtil.getRotationPlayer().getY();
                double z = RotationUtil.getRotationPlayer().getZ();
                module.rotations = RotationUtil.getRotations(
                                crystalPos.getX() + 0.5f,
                                crystalPos.getY() + 1,
                                crystalPos.getZ() + 0.5f,
                                x, y, z,
                                mc.player.getEyeHeight(mc.player.getPose()));

                ray = RotationUtil.rayTraceWithYP(crystalPos, (ClientWorld) helper,
                    module.rotations[0], module.rotations[1],
                    (b, p) -> p.equals(crystalPos));

                if (ray == null)
                {
                    ray = new BlockHitResult(new Vec3d(0.5, 1.0, 0.5),
                                                Direction.UP,
                                                crystalPos,
                                      false);
                }
            }

            int crystalSlot = module.crystalSlot;
            BlockHitResult finalResult = ray;
            float[] f = RayTraceUtil.hitVecToPlaceVec(crystalPos, ray.getPos());
            Hand h = InventoryUtil.getHand(crystalSlot);
            BlockPos finalPos = crystalPos;
            module.post.add(() ->
            {
                module.crystalSwitchBackSlot = crystalSlot;
                module.cooldownBypass.getValue().switchTo(crystalSlot);
                mc.player.networkHandler.sendPacket(
                        new PlayerInteractBlockC2SPacket(
                                h, new BlockHitResult(finalPos.toCenterPos(), finalResult.getSide(), finalPos, false),
                                0
                        ));
                // mc.player.networkHandler.sendPacket(
                //     new PlayerInteractBlockC2SPacket(
                //         finalPos, finalResult.sideHit, h, f[0], f[1], f[2]));
            });
        }

        return super.rotateCheck();
    }

    private void placeObby(BlockPos crystalPos, TargetResult result)
    {
        if (module.crystalSlot == -1)
        {
            module.reset();
            result.setValid(false);
            return;
        }

        List<Entity> entities = new ArrayList<>();
        for (Entity entity : mc.world.getEntities())
        {
            entities.add(entity);
        }

        if (!module.attackTimer.passed(module.itemDeathTime.getValue()))
        {
            entities = entities.stream()
                               .filter(e -> !(e instanceof ItemEntity))
                               .collect(Collectors.toList());
        }

        if (!BlockUtil.canPlaceCrystalReplaceable(crystalPos,
                true, module.newVer.getValue(), entities,
                module.newVerEntities.getValue(), 0))
        {
            module.reset();
            result.setValid(false);
            return;
        }

        BlockState state = mc.world.getBlockState(crystalPos);
        if (state.getBlock() != Blocks.OBSIDIAN
            && state.getBlock() != Blocks.BEDROCK)
        {
            if (!state.isReplaceable()
                || !module.obby.getValue()
                || module.obbySlot == -1)
            {
                module.reset();
                result.setValid(false);
                return;
            }

            result.getTargets().add(crystalPos);
            module.slot = module.obbySlot;
        }

        this.crystalPos = crystalPos;
    }

    private boolean entityCheck(BlockPos pos)
    {
        BlockPos boost1 = pos.up();
        for (Entity entity : mc.world.getOtherEntities(
                                                    new EndCrystalEntity(mc.world, pos.getX(), pos.getY(), pos.getZ()),
                                                    new Box(boost1)))
        {
            if (entity == null || EntityUtil.isDead(entity))
            {
                continue;
            }

            return true;
        }

        return false;
    }

}
