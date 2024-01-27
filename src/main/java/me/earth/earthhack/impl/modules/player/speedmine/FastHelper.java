package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastHelper implements Globals {
    private final StopWatch timer = new StopWatch();
    private final Speedmine module;
    protected boolean sendAbortNextTick;

    public FastHelper(Speedmine module) {
        this.module = module;
    }

    public void reset() {
        timer.reset();
    }

    public void onBlockChange(BlockPos pos, BlockState state) {
        mc.execute(() -> {
            if (module.sentPacket && pos.equals(module.pos)) {
                module.sentPacket = false;
                timer.reset();

                if (state.getBlock() != Blocks.AIR
                        && module.resetFastOnAir.getValue()
                    || state.getBlock() != Blocks.AIR
                        && module.resetFastOnNonAir.getValue()) {
                    PlayerActionC2SPacket abort = new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                        module.pos, module.facing);

                    if (module.event.getValue()) {
                        mc.player.networkHandler.sendPacket(abort);
                    } else {
                        NetworkUtil.sendPacketNoEvent(abort, false);
                    }

                    module.reset();
                }
            }
        });
    }

    public void sendAbortStart(BlockPos pos, Direction facing) {
        timer.reset();
        BlockPos abortPos = pos.equals(mc.player.getBlockPos())
            ? mc.player.getBlockPos().up()
            : mc.player.getBlockPos();
        PlayerActionC2SPacket abort = new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
            abortPos, facing);

        PlayerActionC2SPacket start = new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
            pos, facing);

        PlayerActionC2SPacket stop = new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
            pos, facing);

        if (module.event.getValue()) {
            mc.player.networkHandler.sendPacket(abort);
            mc.player.networkHandler.sendPacket(start);
            mc.player.networkHandler.sendPacket(stop);
        } else {
            NetworkUtil.sendPacketNoEvent(abort, false);
            NetworkUtil.sendPacketNoEvent(start, false);
            NetworkUtil.sendPacketNoEvent(stop, false);
        }
    }

    public void onUpdate() {
        if (!MineUtil.canBreak(module.pos)) {
            return;
        }

        if (sendAbortNextTick
            && module.abortNextTick.getValue()
            && timer.passed(25)) {
            sendAbortNextTick = false;
            PlayerActionC2SPacket abort = new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                module.pos, Direction.DOWN);
            if (module.event.getValue()) {
                mc.player.networkHandler.sendPacket(abort);
            } else {
                NetworkUtil.sendPacketNoEvent(abort, false);
            }
        }

        module.maxDamage = 0.0f;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            float damage = 0.0f;
            long ticks = timer.getTime() / module.tickTime.getValue();
            for (Boolean onGround : module.ongroundHistoryHelper) {
                if (ticks-- <= 0) {
                    break;
                }

                damage += MineUtil.getDamage(stack, module.pos,
                                             module.onGround.getValue(),
                                             onGround)
                    * (module.tpsSync.getValue()
                        ? Managers.TPS.getFactor()
                        : 1.0f);
            }

            while (ticks-- > 0) {
                damage += MineUtil.getDamage(stack, module.pos,
                                             module.onGround.getValue(),
                                             true)
                    * (module.tpsSync.getValue()
                        ? Managers.TPS.getFactor()
                        : 1.0f);
            }

            module.damages[i] = MathUtil.clamp(damage, 0.0f, Float.MAX_VALUE);
            if (module.damages[i] > module.maxDamage) {
                module.maxDamage = module.damages[i];
            }
        }

        int fastSlot = module.getFastSlot();
        boolean prePlace = false;
        if ((module.damages[mc.player.getInventory().selectedSlot] >= module.limit.getValue()
            || module.swap.getValue() && fastSlot != -1
            || (prePlace = module.prePlaceCheck()))
            && (!module.checkPacket.getValue() || !module.sentPacket))
        {
            boolean finalPrePlace = prePlace;
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
            {
                int crystalSlot;
                BlockPos crystalPos;
                boolean swap = module.swap.getValue();
                int lastSlot = mc.player.getInventory().selectedSlot;
                if (module.placeCrystal.getValue()
                    && ((crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL)) != -1
                        || module.offhandPlace.getValue())
                    && (crystalPos = module.crystalHelper.calcCrystal(module.pos)) != null
                    && module.crystalHelper.doCrystalPlace(crystalPos, crystalSlot, lastSlot, swap, finalPrePlace)
                        || finalPrePlace)
                {
                    return;
                }

                module.postCrystalPlace(fastSlot, lastSlot, swap);
            });
        }
    }

}
