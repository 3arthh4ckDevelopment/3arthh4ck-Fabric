package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceResult;
import me.earth.earthhack.impl.util.minecraft.ArmUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

//TODO: maybe account for Tps?
final class ListenerUpdate extends ModuleListener<Speedmine, UpdateEvent>
{
    // private static final ModuleCache<Nuker> NUKER =
    //         Caches.getModule(Nuker.class);
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
            Caches.getModule(AutoCrystal.class);
    // private static final ModuleCache<AnvilAura> ANVIL_AURA =
    //         Caches.getModule(AnvilAura.class);
    // private static final SettingCache<Boolean, BooleanSetting, Nuker> NUKE =
    //         Caches.getSetting(Nuker.class, BooleanSetting.class, "Nuke", false);

    public ListenerUpdate(Speedmine module)
    {
        super(module, UpdateEvent.class, -10);
    }

    private PlayerEntity getPlacePlayer(BlockPos pos)
    {
        for (PlayerEntity player : mc.world.getPlayers())
        {
            if (Managers.FRIENDS.contains(player) || player == mc.player) continue;
            final BlockPos playerPos = PositionUtil.getPosition(player);
            for (Direction facing : Direction.HORIZONTAL)
            {
                if (playerPos.offset(facing).equals(pos))
                {
                    return player;
                }
            }
            if (playerPos.offset(Direction.UP).offset(Direction.UP).equals(pos))
            {
                return player;
            }
        }

        return null;
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        // if (PingBypass.isConnected() && !event.isPingBypass())
        // {
        //     return;
        // }

        module.checkReset();
        if (PlayerUtil.isCreative(mc.player)
                /*|| NUKER.isEnabled() && NUKE.getValue()
                || ANVIL_AURA.isEnabled() && ANVIL_AURA.get().isMining()*/)
        {
            return;
        }

        // if (!PlayerUtil.isCreative(mc.player)
        //     && PingBypass.isConnected()
        //     && module.esp.getValue() != ESPMode.None
        //     && module.bb != null)
        // {
        //     PingBypass.sendPacket(new S2CRenderPacket(
        //         module.bb, module.pbOutline.getValue(),
        //         module.pbColor.getValue()));
        // }

        ((IClientPlayerInteractionManager) mc.interactionManager).setBlockHitDelay(0);
        if (!module.multiTask.getValue()
                && (module.noReset.getValue()
                    || module.mode.getValue() == MineMode.Reset)
                && mc.options.useKey.isPressed())
        {
            ((IClientPlayerInteractionManager) mc.interactionManager)
                    .setIsHittingBlock(false);
        }

        if (module.pos != null)
        {
            if ((module.mode.getValue() == MineMode.Smart
                        || module.mode.getValue() == MineMode.Fast
                        || module.mode.getValue() == MineMode.Instant
                        || module.mode.getValue() == MineMode.Civ)
                    && mc.player.squaredDistanceTo(module.pos.toCenterPos()) >
                            MathUtil.square(module.range.getValue()))
            {
                module.abortCurrentPos();
                return;
            }

            if (module.mode.getValue() == MineMode.Fast) {
                module.fastHelper.onUpdate();
                return;
            }

            if (module.mode.getValue() == MineMode.Civ
                    && module.facing != null
                    && !BlockUtil.isAir(module.pos)
                    && !module.isPausing()
                    && module.delayTimer.passed(module.realDelay.getValue()))
            {
                ArmUtil.swingPacket(Hand.MAIN_HAND);
                module.sendStopDestroy(module.pos, module.facing, false);
            }

            module.updateDamages();
            if (module.normal.getValue())
            {
                int fastSlot = module.getFastSlot();
                boolean prePlace = false;
                if ((module.damages[mc.player.inventory.selectedSlot] >= module.limit.getValue()
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
                        int lastSlot = mc.player.inventory.selectedSlot;

                        if (module.placeCrystal.getValue()
                                && (crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL)) != -1
                                && (crystalPos = module.crystalHelper.calcCrystal(module.pos)) != null
                                && module.crystalHelper.doCrystalPlace(crystalPos, crystalSlot, lastSlot, swap, finalPrePlace)
                                    || finalPrePlace)
                        {
                            return;
                        }

                        module.postCrystalPlace(fastSlot, lastSlot, swap);
                    });
                }

                return;
            }

            int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
            if ((module.damages[mc.player.inventory.selectedSlot] >=
                            module.limit.getValue())
                    || (pickSlot >= 0 && module.damages[pickSlot] >= module.limit.getValue())
                    && !module.pausing
                    && module.breakBind.getValue().getKey() == -1)
            {
                int lastSlot = mc.player.inventory.selectedSlot;
                final PlayerEntity placeTarg = getPlacePlayer(module.pos);
                if (placeTarg != null) {
                    final BlockPos p = PlayerUtil.getBestPlace(module.pos, placeTarg);
                    if (module.placeCrystal.getValue() && AUTOCRYSTAL.isEnabled() && p != null && BlockUtil.canPlaceCrystal(p,false,false)) {
                        final RayTraceResult result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, p);

                        if (mc.player.getOffHandStack() != ItemStack.EMPTY && mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL) {
                            final PlayerInteractBlockC2SPacket place =
                                    new PlayerInteractBlockC2SPacket(Hand.OFF_HAND, new BlockHitResult(result.hitVec, result.sideHit, p, false), 0);
                            final HandSwingC2SPacket animation =
                                    new HandSwingC2SPacket(Hand.OFF_HAND);
                            InventoryUtil.syncItem();
                            mc.player.networkHandler.sendPacket(place);
                            if (AUTOCRYSTAL.isPresent()) {
                                AUTOCRYSTAL.get().placed.put(p.up(), new CrystalTimeStamp(Float.MAX_VALUE, false));
                                AUTOCRYSTAL.get().bombPos = p.up();
                            }
                            mc.player.networkHandler.sendPacket(animation);
                        } else {
                            final int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                            if (crystalSlot != -1) {
                                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
                                    module.cooldownBypass.getValue().switchTo(crystalSlot);
                                    final PlayerInteractBlockC2SPacket place =
                                        new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(result.hitVec, result.sideHit, p, false), 0);
                                    final HandSwingC2SPacket animation =
                                        new HandSwingC2SPacket(Hand.MAIN_HAND);
                                    mc.player.networkHandler.sendPacket(place);
                                    mc.player.networkHandler.sendPacket(animation);
                                    module.cooldownBypass.getValue().switchBack(lastSlot, crystalSlot);
                                });
                            }
                        }
                    }
                }

                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    if (module.swap.getValue())
                    {
                        module.cooldownBypass.getValue().switchTo(pickSlot);
                    }

                    NetworkUtil.sendPacketNoEvent(
                        new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                            module.pos,
                            module.facing),
                        false);

                    if (module.swap.getValue())
                    {
                        module.cooldownBypass.getValue().switchBack(
                            lastSlot, pickSlot);
                    }
                });

                if (module.toAir.getValue())
                {
                    mc.interactionManager.breakBlock(module.pos);
                }

                module.onSendPacket();
            }
        }
    }

}
