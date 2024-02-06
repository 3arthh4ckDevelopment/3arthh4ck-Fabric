package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AntiWeakness;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.AutoSwitch;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.PositionData;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.WeaknessSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationSmoother;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.MutableWrapper;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.AtomicInteger;

// TODO: make the resetting better!
public class HelperRotation implements Globals
{
    private static final AtomicInteger ID = new AtomicInteger();
    private static final ModuleCache<Offhand> OFFHAND =
            Caches.getModule(Offhand.class);
    private static final ModuleCache<Suicide> SUICIDE =
        Caches.getModule(Suicide.class);

    private final RotationSmoother smoother;
    private final AutoCrystal module;

    public HelperRotation(AutoCrystal module)
    {
        this.smoother = new RotationSmoother(Managers.ROTATION);
        this.module = module;
    }

    public RotationFunction forPlacing(BlockPos pos,
                                       MutableWrapper<Boolean> hasPlaced)
    {
        int id = ID.incrementAndGet();
        StopWatch timer = new StopWatch();
        MutableWrapper<Boolean> ended = new MutableWrapper<>(false);
        return (x, y, z, yaw, pitch) ->
        {
            boolean breaking = false;
            float[] rotations = null;
            if (hasPlaced.get()
                || RotationUtil.getRotationPlayer().squaredDistanceTo(pos.toCenterPos()) > 64
                    && pos.getSquaredDistance(x, y, z) > 64
                || (module.autoSwitch.getValue() != AutoSwitch.Always
                    && !module.switching
                    && !module.weaknessHelper.canSwitch()
                    && !InventoryUtil.isHolding(Items.END_CRYSTAL)))
            {
                if (!ended.get())
                {
                    ended.set(true);
                    timer.reset();
                }

                if (!module.attack.getValue()
                        || timer.passed(module.endRotations.getValue()))
                {
                    if (id == ID.get())
                    {
                        module.rotation = null;
                    }

                    return new float[] { yaw, pitch };
                }

                breaking = true;
                double height = 1.7 * module.height.getValue();
                rotations =
                    RotationUtil.getRotations(pos.getX() + 0.5f,
                                              pos.getY() + 1 + height,
                                              pos.getZ() + 0.5f,
                                              x, y, z,
                                              mc.player.getEyeHeight(mc.player.getPose()));
            }
            else
            {
                double height = module.placeHeight.getValue();
                if (module.smartTrace.getValue())
                {
                    for (Direction facing : Direction.values())
                    {
                        Ray ray = RayTraceFactory.rayTrace(
                                           mc.player,
                                           pos,
                                           facing,
                                           mc.world,
                                           Blocks.OBSIDIAN.getDefaultState(),
                                           module.traceWidth.getValue());
                        if (ray.isLegit())
                        {
                            rotations = ray.getRotations();
                            break;
                        }
                    }
                }

                if (rotations == null)
                {
                    if (module.fallbackTrace.getValue())
                    {
                        rotations = RotationUtil.getRotations(
                                pos.getX() + 0.5,
                                pos.getY() + 1.0,
                                pos.getZ() + 0.5,
                                x, y, z,
                                mc.player.getEyeHeight(mc.player.getPose()));
                    }
                    else
                    {
                        rotations = RotationUtil.getRotations(
                                pos.getX() + 0.5,
                                pos.getY() + height,
                                pos.getZ() + 0.5,
                                x, y, z,
                                mc.player.getEyeHeight(mc.player.getPose()));
                    }
                }
            }

            return smoother.smoothen(rotations,
                    breaking ? module.angle.getValue()
                            :  module.placeAngle.getValue());
        };
    }

    public RotationFunction forBreaking(Entity entity,
                                        MutableWrapper<Boolean> attacked)
    {
        int id = ID.incrementAndGet();
        StopWatch timer = new StopWatch();
        MutableWrapper<Boolean> ended = new MutableWrapper<>(false);
        return (x, y, z, yaw, pitch) ->
        {
            if (RotationUtil.getRotationPlayer().squaredDistanceTo(entity) > 64)
            {
                attacked.set(true);
            }

            if (module.getTarget() == null)
            {
                attacked.set(true);
            }

            if (attacked.get())
            {
                if (!ended.get())
                {
                    ended.set(true);
                    timer.reset();
                }

                if (ended.get()
                        && timer.passed(module.endRotations.getValue()))
                {
                    if (id == ID.get())
                    {
                        module.rotation = null;
                    }

                    return new float[] { yaw, pitch };
                }
            }

            return smoother.getRotations(entity, x, y, z,
                                         mc.player.getEyeHeight(mc.player.getPose()),
                                         module.height.getValue(),
                                         module.angle.getValue());
        };
    }

    public RotationFunction forObby(PositionData data)
    {
        return (x,y,z,yaw,pitch) ->
        {
            if (data.getPath().length <= 0)
            {
                return new float[]{ yaw, pitch };
            }

            Ray ray = data.getPath()[0];
            ray.updateRotations(RotationUtil.getRotationPlayer());
            return ray.getRotations();
        };
    }

    public Runnable post(AutoCrystal module,
                         float damage,
                         boolean realtime,
                         BlockPos pos,
                         MutableWrapper<Boolean> placed,
                         MutableWrapper<Boolean> liquidBreak)
    {
        return () ->
        {
            if (liquidBreak != null && !liquidBreak.get())
            {
                return;
            }

            if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
            {
                if (module.autoSwitch.getValue() == AutoSwitch.Always
                    || module.autoSwitch.getValue() == AutoSwitch.Bind
                            && module.switching)
                {
                    if (!module.mainHand.getValue())
                    {
                        OFFHAND.computeIfPresent(o ->
                                o.setMode(OffhandMode.CRYSTAL));

                        if (module.instantOffhand.getValue())
                        {
                            if (OFFHAND.get().isSafe()
                                || SUICIDE.returnIfPresent(Suicide::deactivateOffhand, false))
                            {
                                OFFHAND.get().setMode(OffhandMode.CRYSTAL);

                                for (int i = 0; i < 3; i++)
                                {
                                    OFFHAND.get().getTimer().setTime(10000);
                                    OFFHAND.get().doOffhand();
                                }
                            }

                            if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
                            {
                                return;
                            }
                        }
                        else
                        {
                            return;
                        }
                    }
                }
                else
                {
                    return;
                }
            }

            int slot = -1;
            Hand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
            if (hand == null)
            {
                if (module.mainHand.getValue())
                {
                    slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                    if (slot == -1)
                    {
                        return;
                    }
                    // -2 shouldn't really happen, but just to be safe
                    hand = slot == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND;
                }
                else
                {
                    return;
                }
            }

            BlockHitResult ray = RotationUtil.rayTraceTo(pos, mc.world);
            if (ray == null || !pos.equals(ray.getBlockPos()))
            {
                if (!module.rotate.getValue().noRotate(ACRotate.Place)
                    && !module.isNotCheckingRotations())
                {
                    return;
                }

                ray = new BlockHitResult(
                        new Vec3d(0.5, 1.0, 0.5), Direction.UP, pos, false);
            }
            else if (module.fallbackTrace.getValue()
                && mc.world.getBlockState(ray.getBlockPos().offset(ray.getSide()))
                           .isSolid())
            {
                ray = new BlockHitResult(
                        new Vec3d(0.5, 1.0, 0.5), Direction.UP, pos, false);
            }

            module.switching = false;
            SwingTime swingTime = module.placeSwing.getValue();
            float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.getPos());
            boolean noGodded = false;
            // we need to check this here since we switch
            if (module.idHelper.isDangerous(mc.player,
                                            module.holdingCheck.getValue(),
                                            module.toolCheck.getValue()))
            {
                module.noGod = true;
                noGodded = true;
            }

            int finalSlot = slot;
            Hand finalHand = hand;
            BlockHitResult finalRay = ray;
            boolean finalNoGodded = noGodded;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.getInventory().selectedSlot;
                if (finalSlot != -1 && finalSlot != -2)
                {
                    module.cooldownBypass.getValue().switchTo(finalSlot);
                }

                InventoryUtil.syncItem();
                if (swingTime == SwingTime.Pre)
                {
                    swing(finalHand, false);
                }

                mc.player.networkHandler.sendPacket(
                    new PlayerInteractBlockC2SPacket(finalHand, new BlockHitResult(new Vec3d(f[0], f[1], f[2]), finalRay.getSide(), pos, false), 0));
                module.sequentialHelper.setExpecting(pos);

                if (finalNoGodded)
                {
                    module.noGod = false;
                }

                placed.set(true);

                if (swingTime == SwingTime.Post)
                {
                    swing(finalHand, false);
                }

                if (module.switchBack.getValue())
                {
                    module.cooldownBypass.getValue()
                                         .switchBack(lastSlot, finalSlot);
                }
            });

            if (realtime)
            {
                module.setRenderPos(pos, damage);
            }

            if (module.simulatePlace.getValue() != 0)
            {
                module.crystalRender.addFakeCrystal(
                    new EndCrystalEntity(mc.world, pos.getX() + 0.5f,
                                                     pos.getY() + 1,
                                                     pos.getZ() + 0.5f));
            }
        };
    }

    public Runnable post(Entity entity, MutableWrapper<Boolean> attacked)
    {
        return () ->
        {
            WeaknessSwitch w = antiWeakness(module);
            if (w.needsSwitch() && w.getSlot() == -1
                || EntityUtil.isDead(entity)
                || !module.rotate.getValue().noRotate(ACRotate.Break)
                    && !module.isNotCheckingRotations()
                    && !RotationUtil.isLegit(entity))
            {
                return;
            }

            PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking());
            SwingTime swingTime = module.breakSwing.getValue();
            Runnable runnable = () ->
            {
                int lastSlot = mc.player.getInventory().selectedSlot;
                if (w.getSlot() != -1)
                {
                    module.antiWeaknessBypass.getValue().switchTo(w.getSlot());
                }

                if (swingTime == SwingTime.Pre)
                {
                    swing(Hand.MAIN_HAND, true);
                }

                mc.player.networkHandler.sendPacket(packet);
                attacked.set(true);

                if (swingTime == SwingTime.Post)
                {
                    swing(Hand.MAIN_HAND, true);
                }

                if (w.getSlot() != -1)
                {
                    module.antiWeaknessBypass.getValue().switchBack(
                        lastSlot, w.getSlot());
                }
            };

            if (w.getSlot() != -1)
            {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, runnable);
            }
            else
            {
                runnable.run();
            }

            if (module.pseudoSetDead.getValue())
            {
                ((IEntity) entity).setPseudoDead(true);
            }

            if (module.setDead.getValue())
            {
                Managers.SET_DEAD.setDead(entity);
            }
        };
    }

    public Runnable postBlock(PositionData data)
    {
        return postBlock(data, -1, module.obbyRotate.getValue(), null, null);
    }

    public Runnable postBlock(PositionData data,
                              int preSlot,
                              Rotate rotate,
                              MutableWrapper<Boolean> placed,
                              MutableWrapper<Integer> switchBack)
    {
        return () ->
        {
            if (!data.isValid())
            {
                return;
            }

            int slot = preSlot == -1
                            ? InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)
                            : preSlot;
            if (slot == -1)
            {
                return;
            }

            Hand hand = slot == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            PlaceSwing placeSwing = module.obbySwing.getValue();

            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.getInventory().selectedSlot;
                if (switchBack != null)
                {
                    switchBack.set(lastSlot);
                }

                module.obsidianBypass.getValue().switchTo(slot);

                for (Ray ray : data.getPath())
                {
                    if (rotate == Rotate.Packet
                        && !RotationUtil.isLegit(ray.getPos(), ray.getFacing()))
                    {
                        Managers.ROTATION.setBlocking(true);
                        float[] r = ray.getRotations();
                        //PingBypass.sendToActualServer(
                        PacketUtil.rotation(r[0], r[1], mc.player.isOnGround()); //);

                        Managers.ROTATION.setBlocking(false);
                    }

                    float[] f = RayTraceUtil.hitVecToPlaceVec(
                            ray.getPos(), ray.getResult().getPos());

                    mc.player.networkHandler.sendPacket(
                            new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(ray.getVector(),
                                    ray.getFacing(), ray.getPos(), false), 0));

                    if (module.setState.getValue() && preSlot == -1)
                    {
                        mc.execute(() ->
                        {
                            if (mc.world != null)
                            {
                                mc.world.setBlockState(
                                        ray.getPos().offset(ray.getFacing()),
                                        Blocks.OBSIDIAN.getDefaultState());
                            }
                        });
                    }

                    if (placeSwing == PlaceSwing.Always)
                    {
                        Swing.Packet.swing(hand);
                    }
                }

                if (placeSwing == PlaceSwing.Once)
                {
                    Swing.Packet.swing(hand);
                }

                module.obsidianBypass.getValue().switchBack(lastSlot, slot);

                if (placed != null)
                {
                    placed.set(true);
                }
            });

            Hand swingHand = module.obbyHand.getValue().getHand();
            if (swingHand != null)
            {
                Swing.Client.swing(swingHand);
            }
        };
    }

    public Runnable breakBlock(int toolSlot,
                               MutableWrapper<Boolean> placed,
                               MutableWrapper<Integer> lastSlot,
                               int[] order,
                               Ray...positions)
    {
        return Locks.wrap(Locks.PLACE_SWITCH_LOCK, () ->
        {
            if (order.length != positions.length)
            {
                throw new IndexOutOfBoundsException("Order length: "
                    + order.length + ", Positions length: " + positions.length);
            }

            if (!placed.get())
            {
                return;
            }

            module.mineBypass.getValue().switchTo(toolSlot);

            for (int i : order)
            {
                Ray ray = positions[i];
                BlockPos pos = ray.getPos().offset(ray.getFacing());
                PacketUtil.startDigging(pos, ray.getFacing().getOpposite());
                PacketUtil.stopDigging( pos, ray.getFacing().getOpposite());
                Swing.Packet.swing(Hand.MAIN_HAND);
            }

            module.mineBypass.getValue().switchBack(lastSlot.get(), toolSlot);
        });
    }

    public void swing(Hand hand, boolean breaking)
    {
        Swing.Packet.swing(hand);
        Hand swingHand = breaking
                ? module.swing.getValue().getHand()
                : module.placeHand.getValue().getHand();

        if (swingHand != null)
        {
            Swing.Client.swing(swingHand);
        }
    }

    public static WeaknessSwitch antiWeakness(AutoCrystal module)
    {
        if (!module.weaknessHelper.isWeaknessed())
        {
            return WeaknessSwitch.NONE;
        }
        else if (module.antiWeakness.getValue() == AntiWeakness.None
                || module.cooldown.getValue() != 0)
        {
            return WeaknessSwitch.INVALID;
        }

        return new WeaknessSwitch(DamageUtil.findAntiWeakness(), true);
    }

}
