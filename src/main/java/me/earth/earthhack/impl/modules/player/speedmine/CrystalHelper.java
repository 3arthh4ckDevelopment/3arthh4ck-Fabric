package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.CrystalTimeStamp;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceResult;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class CrystalHelper implements Globals {
    private static final Vec3i[] CRYSTAL_OFFSETS = new Vec3i[]
        {
            new Vec3i(1, -1, 0),
            new Vec3i(0, -1, 1),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, -1, -1),
            new Vec3i(0, 0, 0) // check this one last!
        };
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
        Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);
    private final IBlockStateHelper helper = new BlockStateHelper();

    private final Speedmine module;

    public CrystalHelper(Speedmine module) {
        this.module = module;
    }

    public BlockPos calcCrystal(BlockPos mined)
    {
        return calcCrystal(mined, null, false);
    }

    public BlockPos calcCrystal(BlockPos mined, PlayerEntity player, boolean ignoreCrystals)
    {
        helper.clearAllStates();
        helper.addAir(mined);
        DamageResult result = new DamageResult();
        result.bestDamage = Float.MIN_VALUE;

        List<Entity> entities = new ArrayList<>();
        // hacky
        for (Entity entity : mc.world.getEntities())
        {
            entities.add(entity);
        }

        for (Vec3i offset : CRYSTAL_OFFSETS)
        {
            BlockPos pos = mined.add(offset);
            if (BlockUtil.isCrystalPosInRange(pos, module.crystalRange.getValue(), module.crystalTrace.getValue(),
                                              module.crystalBreakTrace.getValue())
                && BlockUtil.canPlaceCrystal(pos, ignoreCrystals, module.oldVer.getValue(), entities,
                                             module.newVerEntities.getValue(), 0L))
            {
                float selfDamage = DamageUtil.calculate(pos, mc.player, mc.world);
                if (selfDamage > module.maxSelfDmg.getValue())
                {
                    continue;
                }

                if (player == null)
                {
                    for (PlayerEntity p : mc.world.getPlayers())
                    {
                        checkPlayer(p, pos, result);
                    }
                }
                else
                {
                    checkPlayer(player, pos, result);
                }
            }
        }

        return result.bestPos;
    }

    private void checkPlayer(PlayerEntity player,
                             BlockPos pos,
                             DamageResult result) {
        if (player != null
            && !player.equals(mc.player)
            && !player.equals(RotationUtil.getRotationPlayer())
            && !Managers.FRIENDS.contains(player)
            && !EntityUtil.isDead(player)
            && player.squaredDistanceTo(pos.toCenterPos()) < 144)
        {
            float damage = DamageUtil.calculate(pos, player, (ClientWorld) helper);
            if (damage > module.minDmg.getValue() && damage > result.bestDamage)
            {
                result.bestPos = pos;
                result.bestDamage = damage;
            }
        }
    }

    public void placeCrystal(BlockPos pos, int slot, RayTraceResult ray, boolean prePlace)
    {
        Hand hand = module.offhandPlace.getValue()
            ? Hand.OFF_HAND
            : InventoryUtil.getHand(slot);

        float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.hitVec);
        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            OffhandMode mode = null;
            if (slot != -2)
            {
                if (module.offhandPlace.getValue())
                {
                    mode = OFFHAND.get().getMode();
                    OFFHAND.get().forceMode(OffhandMode.CRYSTAL);
                    if (OFFHAND.get().getMode() != OffhandMode.CRYSTAL)
                    {
                        return;
                    }
                }
                else
                {
                    module.cooldownBypass.getValue().switchTo(slot);
                }
            }

            if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Pre)
            {
                AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
            }

            mc.player.networkHandler.sendPacket(
                new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(new Vec3d(f[0], f[1], f[2]), ray.sideHit, pos, false), 0));

            if (AUTOCRYSTAL.get().placeSwing.getValue() == SwingTime.Post)
            {
                AUTOCRYSTAL.get().rotationHelper.swing(hand, false);
            }

            if (module.offhandSilent.getValue() && mode != null)
            {
                OFFHAND.get().setMode(mode);
                if (module.megaSilent.getValue())
                {
                    OFFHAND.get().forceMode(mode);
                }
            }
        });

        if (AUTOCRYSTAL.isPresent() && !prePlace)
        {
            AUTOCRYSTAL.get().placed.put(
                pos.up(), new CrystalTimeStamp(Float.MAX_VALUE, false));
            AUTOCRYSTAL.get().bombPos = pos.up();
        }
    }

    public boolean doCrystalPlace(BlockPos crystalPos, int crystalSlot, int lastSlot, boolean swap, boolean prePlace)
    {
        if (module.antiAntiSilentSwitch.getValue()
            && !module.aASSSwitchTimer.passed(module.aASSwitchTime.getValue()))
        {
            return true;
        }

        RayTraceResult ray = RotationUtil.rayTraceTo(crystalPos, mc.world);
        if (ray != null && ray.sideHit != null && ray.hitVec != null)
        {
            module.crystalHelper.placeCrystal(crystalPos, crystalSlot, ray, prePlace);
            boolean swappedBack = false;
            if (!swap || module.rotate.getValue()
                && module.limitRotations.getValue()
                && !RotationUtil.isLegit(module.pos, module.facing))
            {
                swappedBack = true;
                // TODO:?????????????????????????
                module.cooldownBypass.getValue().switchBack(
                    lastSlot, crystalSlot);
            }

            if (module.antiAntiSilentSwitch.getValue()) {
                if (!swappedBack && !module.offhandPlace.getValue()) {
                    module.cooldownBypass.getValue().switchBack(
                        lastSlot, crystalSlot);
                }

                module.aASSSwitchTimer.reset();
                return true;
            }
        }

        return false;
    }

    private static final class DamageResult
    {
        public BlockPos bestPos;
        public float bestDamage;
    }

}
