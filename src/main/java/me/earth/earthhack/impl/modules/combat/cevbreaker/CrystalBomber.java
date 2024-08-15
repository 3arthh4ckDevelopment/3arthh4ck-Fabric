package me.earth.earthhack.impl.modules.combat.cevbreaker;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.cevbreaker.enums.CrystalBomberMode;
import me.earth.earthhack.impl.modules.combat.cevbreaker.enums.CrystalBomberStage;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalBomber extends Module {

    protected final Setting<CrystalBomberMode> mode =
            register(new EnumSetting<>("Mode", CrystalBomberMode.Normal));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.1f, 6.0f));
    protected final Setting<Float> toggleAt =
            register(new NumberSetting<>("ToggleAt", 8.0f, 0.1f, 20.0f)); // not used?
    protected final Setting<Float> enemyRange =
            register(new NumberSetting<>("EnemyRange", 6.0f, 0.1f, 16.0f));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 0, 0, 500));
    protected final Setting<Integer> cooldown =
            register(new NumberSetting<>("Cooldown", 0, 0, 500));
    protected final Setting<Boolean> rotate =
            register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> reCheckCrystal =
            register(new BooleanSetting("ReCheckCrystal", false));
    protected final Setting<Boolean> airCheck =
            register(new BooleanSetting("AirCheck", false));
    protected final Setting<Boolean> smartSneak =
            register(new BooleanSetting("Smart-Sneak", true));
    protected final Setting<Boolean> bypass =
            register(new BooleanSetting("Bypass", false));

    private static final ModuleCache<Speedmine> SPEEDMINE =
            Caches.getModule(Speedmine.class);

    private static PlayerEntity target;
    private Vec3d lastTargetPos;
    private BlockPos targetPos;
    private int lastSlot;
    public boolean rotating = false;
    private boolean offhand;

    // ^^ so much unused shit... maybe implement this?
    private final StopWatch timer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();
    private final StopWatch cooldownTimer = new StopWatch();
    protected final StopWatch targetTimer = new StopWatch();

    private CrystalBomberStage stage = CrystalBomberStage.FirstHit;
    private boolean firstHit = false;

    public CrystalBomber() {
        super("CrystalBomber", Category.Combat);
        this.setData(new CrystalBomberData(this));
        this.listeners.add(new ListenerMotion(this));
    }

    @Override
    protected void onEnable() {
        targetPos = null;
        lastTargetPos = null;
        target = null;
        stage = CrystalBomberStage.FirstHit;
        timer.reset();
        targetTimer.reset();
        delayTimer.reset();
        cooldownTimer.reset();
        /*if (fullOffhand.getValue()) {
            Speedmine.getInstance().pausing = true;
        }*/
    }

    protected void doCevBreaker(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            updateTarget();
            if (target != null) {
                if (targetPos != null) {
                    lastTargetPos = targetPos.toCenterPos();
                }
                targetPos = PositionUtil.getPosition(target).up().up();
                if (lastTargetPos != null && !lastTargetPos.equals(targetPos.toCenterPos())) {
                    stage = CrystalBomberStage.FirstHit;
                    firstHit = true;
                }
                if (delayTimer.passed(delay.getValue())) {
                    if (reCheckCrystal.getValue()) recheckCrystal();
                    switch (stage) {
                        case FirstHit:
                            if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR && MineUtil.canBreak(targetPos)) {
                                rotateToPos(targetPos, event);
                                break;
                            }
                        case Crystal:
                            if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                if (BlockUtil.canPlaceCrystal(targetPos, false, false)) {
                                    rotateToPos(targetPos, event);
                                    break;
                                }
                            } else {
                                stage = CrystalBomberStage.PlaceObsidian;
                                delayTimer.reset();
                                break;
                            }
                            break;
                        case Pickaxe:
                            if (firstHit) {
                                if (isValidForMining()) {
                                    rotateToPos(targetPos, event);
                                    break;
                                }
                            } else {
                                rotateToPos(targetPos, event);
                                break;
                            }
                            break;
                        case Explode:
                            List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class,
                                    new Box(targetPos.up()), e -> true);
                            if (!crystals.isEmpty()) {
                                if ((mc.world.getBlockState(targetPos).getBlock() == Blocks.AIR || !airCheck.getValue()) && cooldownTimer.passed(cooldown.getValue())) {
                                    EndCrystalEntity crystal = (EndCrystalEntity) crystals.get(0);
                                    if (crystal != null) {
                                        rotateTo(crystal, event);
                                        break;
                                    }
                                }
                            } else {
                                if (reCheckCrystal.getValue()) {
                                    stage = CrystalBomberStage.Crystal;
                                    delayTimer.reset();
                                    break;
                                }
                            }
                        case PlaceObsidian:
                            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
                            boolean offhand = mc.player.getOffHandStack().getItem() instanceof BlockItem && ((BlockItem) mc.player.getOffHandStack().getItem()).getBlock() == Blocks.OBSIDIAN;
                            if (obbySlot != -1 || offhand) {
                                if (BlockUtil.isReplaceable(targetPos)) {
                                    if (mc.player.squaredDistanceTo(targetPos.toCenterPos()) <= MathUtil.square(range.getValue())) {
                                        rotateToPos(targetPos, event);
                                        break;
                                    }
                                } else if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    break;
                                }
                            }
                    }
                }
            }
        } else if (event.getStage() == Stage.POST) {
            updateTarget();
            if (target != null) {
                if (delayTimer.passed(delay.getValue())) {
                    switch (stage) {
                        case FirstHit:
                            if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR /*|| hitAir.getValue()) && BlockUtil.canBreakNoAir(targetPos)*/) {
                                if (SPEEDMINE.get().getPos() == null || !(SPEEDMINE.get().getPos().equals(targetPos))) {
                                    mc.interactionManager.attackBlock(targetPos, mc.player.getHorizontalFacing().getOpposite());
                                } else if (SPEEDMINE.get().getPos().equals(targetPos) 
                                        && (SPEEDMINE.get().getMode() == MineMode.Instant || SPEEDMINE.get().getMode() == MineMode.Civ)) 
                                {
                                    stage = CrystalBomberStage.Crystal;
                                    delayTimer.reset();
                                    timer.reset();
                                    firstHit = false;
                                    break;
                                }
                                stage = CrystalBomberStage.Crystal;
                                delayTimer.reset();
                                timer.reset();
                                firstHit = true;
                                break;
                            }
                        case Crystal:
                            /*if (offhandSwitch.getValue()) {
                                doOffhandSwitch();
                            }*/
                            int crystalSlot = getCrystalSlot();
                            offhand = mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL;
                            if (!offhand /*&& !offhandSwitch.getValue() || !isValidForOffhand()*/) {
                                lastSlot = mc.player.getInventory().selectedSlot;
                                if (crystalSlot != -1) {
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(crystalSlot);
                                    } else {
                                        InventoryUtil.switchTo(crystalSlot);
                                    }
                                }
                            }
                            if (offhand || mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
                                if (/*BlockUtil.rayTracePlaceCheck(targetPos, true) && mc.player.getDistanceSq(targetPos) <= MathUtil.square(range.getValue()) || */
                                        mc.player.squaredDistanceTo(targetPos.toCenterPos()) <= MathUtil.square(range.getValue())) {
                                    placeCrystalOnBlock(targetPos, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, true, false);
                                }
                            }
                            /*if (!offhand && !offhandSwitch.getValue() && switchBack.getValue() || !isValidForOffhand()) {
                                mc.player.getInventory().selectedSlot = lastSlot;
                                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.getInventory().selectedSlot));
                            }*/
                            stage = CrystalBomberStage.Pickaxe;
                            delayTimer.reset();
                            break;
                        case Pickaxe:
                            if (firstHit) {
                                if (isValidForMining()) {
                                    int pickSlot = getPickSlot();
                                    int lastSlot = mc.player.getInventory().selectedSlot;
                                    if (pickSlot != -1) {
                                        if (bypass.getValue()) {
                                            InventoryUtil.switchToBypass(pickSlot);
                                        } else {
                                            InventoryUtil.switchTo(pickSlot);
                                        }
                                        SPEEDMINE.get().forceSend();
                                        stage = CrystalBomberStage.Explode;
                                        if (bypass.getValue()) {
                                            InventoryUtil.switchToBypass(pickSlot);
                                        } else {
                                            InventoryUtil.switchTo(lastSlot);
                                        }
                                        delayTimer.reset();
                                        cooldownTimer.reset();
                                        firstHit = false;
                                        break;
                                    }
                                }
                            } else {
                                int pickSlot = getPickSlot();
                                int lastSlot = mc.player.getInventory().selectedSlot;
                                if (pickSlot != -1) {
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(pickSlot);
                                    } else {
                                        InventoryUtil.switchTo(pickSlot);
                                    }
                                    SPEEDMINE.get().forceSend();
                                    stage = CrystalBomberStage.Explode;
                                    delayTimer.reset();
                                    cooldownTimer.reset();
                                    if (bypass.getValue()) {
                                        InventoryUtil.switchToBypass(lastSlot);
                                    } else {
                                        InventoryUtil.switchTo(lastSlot);
                                    }
                                    break;
                                }
                            }
                        case Explode:
                            List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class,
                                    new Box(targetPos.up()), e -> true);
                            if (cooldownTimer.passed(cooldown.getValue())) {
                                if (!crystals.isEmpty() && (mc.world.getBlockState(targetPos).getBlock() == Blocks.AIR || !airCheck.getValue())) {
                                    EndCrystalEntity crystal = (EndCrystalEntity) crystals.get(0);
                                    if (crystal != null) {
                                        // rotateTo(crystal);
                                        rotating = false;
                                        if (/*EntityUtil.rayTraceHitCheck(crystal, true) && mc.player.getDistanceSq(crystal) <= MathUtil.square(range.getValue()) || */mc.player.squaredDistanceTo(crystal) <= MathUtil.square(range.getValue())) {
                                            attackEntity(crystal, true, true);
                                            stage = CrystalBomberStage.PlaceObsidian;
                                            delayTimer.reset();
                                            break;
                                        }
                                    } else {
                                        if (reCheckCrystal.getValue()) {
                                            stage = CrystalBomberStage.Crystal;
                                            delayTimer.reset();
                                            break;
                                        }
                                    }
                                } else {
                                    if (reCheckCrystal.getValue()) {
                                        stage = CrystalBomberStage.Crystal;
                                        delayTimer.reset();
                                        break;
                                    }
                                }
                            } else {
                                stage = CrystalBomberStage.Explode;
                                break;
                            }
                        case PlaceObsidian:
                            int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
                            /*if (offhandSwitch.getValue()) {
                                doOffhandObby();
                            }*/
                            boolean offhand = mc.player.getOffHandStack().getItem() instanceof BlockItem item
                                    && item.getBlock() == Blocks.OBSIDIAN;
                            if (obbySlot != -1 || offhand) {
                                if (BlockUtil.isReplaceable(targetPos) || BlockUtil.isAir(targetPos)) {
                                    /*if (!offhand || !isValidForOffhand()) {
                                        mc.player.getInventory().selectedSlot = obbySlot;
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.getInventory().selectedSlot));
                                    }*/
                                    if (mc.player.squaredDistanceTo(targetPos.toCenterPos()) <= MathUtil.square(range.getValue())) {
                                        // rotateToPos(targetPos);
                                        Direction facing = BlockUtil.getFacing(targetPos);
                                        if (facing != null) {
                                            float[] rotations = RotationUtil.getRotations(targetPos.offset(facing), facing.getOpposite());
                                            placeBlock(targetPos.offset(facing), facing.getOpposite(), rotations, obbySlot);
                                        }
                                    }
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    delayTimer.reset();
                                    break;
                                } else if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
                                    if (mode.getValue() == CrystalBomberMode.Instant) {
                                        stage = CrystalBomberStage.Crystal;
                                    } else {
                                        stage = CrystalBomberStage.FirstHit;
                                    }
                                    delayTimer.reset();
                                    break;
                                }
                            }
                    }
                }
            }
        }
    }

    private void updateTarget() {
        List<PlayerEntity> players = mc.world.getPlayers().stream()
                .filter(entity -> mc.player.squaredDistanceTo(entity) <= MathUtil.square(enemyRange.getValue()))
                .filter(entity -> !Managers.FRIENDS.contains(entity))
                .collect(Collectors.toList());
        PlayerEntity currentPlayer = null;
        for (PlayerEntity player : players) {
            if (player == mc.player) continue;
            if (currentPlayer == null) currentPlayer = player;
            if (mc.player.squaredDistanceTo(player) < mc.player.squaredDistanceTo(currentPlayer)) currentPlayer = player;
        }
        target = currentPlayer;
        this.targetTimer.reset();
    }

    public PlayerEntity getTarget()
    {
        if (targetTimer.passed(600))
        {
            target = null;
        }
        return target;
    }
 
    /**
    *
    * @return the currently targeted player & mining progress, haven't tested if this crashes if we don't have SpeedMine on though.
    **/
    @Override
    public String getDisplayInfo()
    {
        return getTarget() == null ? null : getTarget().getName() +
                ", " + SPEEDMINE.get().getDisplayInfo();
    }

    private int getPickSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.DIAMOND_PICKAXE) {
                return (i);
            }
        }
        return -1;
    }

    private int getCrystalSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.END_CRYSTAL) {
                return (i);
            }
        }
        return -1;
    }

    private void rotateTo(Entity entity, MotionUpdateEvent event) {
        if (rotate.getValue()) {
            final float[] angle = RotationUtil.getRotations(entity);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void rotateToPos(BlockPos pos, MotionUpdateEvent event)
    {
        if (rotate.getValue()) {
            final float[] angle = RotationUtil.getRotationsToTopMiddle(pos);
            event.setYaw(angle[0]);
            event.setPitch(angle[1]);
        }
    }

    private void recheckCrystal() {
        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN && mc.world.getEntitiesByClass(EndCrystalEntity.class,
                new Box(targetPos.up()), e -> true).isEmpty() && stage != CrystalBomberStage.FirstHit) {
            stage = CrystalBomberStage.Crystal;
        }
    }

    public static void placeCrystalOnBlock(BlockPos pos, Hand hand, boolean swing, boolean exactHand) {
        BlockHitResult result = mc.world.raycastBlock(
                new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()),
                new Vec3d(pos.getX() + .5, pos.getY() - .5d, pos.getZ() + .5), pos, null, mc.world.getBlockState(pos));
        Direction facing = (result == null || result.getSide() == null) ? Direction.UP : result.getSide();
        NetworkUtil.sendSequenced(seq -> new PlayerInteractBlockC2SPacket(
                hand,
                new BlockHitResult(new Vec3d(0,0,0), facing, pos, false),
                seq));
        
        if (swing) {
            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(exactHand ? hand : Hand.MAIN_HAND));
        }
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(entity, mc.player.isSneaking(), Hand.MAIN_HAND));
        } else {
            mc.interactionManager.attackEntity(mc.player, entity);
        }

        if(swingArm) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    public boolean isValidForMining() {
        int pickSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
        if (pickSlot == -1) return false;
        return SPEEDMINE.get().damages[pickSlot] >= SPEEDMINE.get().limit.getValue();
    }

    /**
     * Places a block on the given position
     * and also tries to lag us back into the block.
     *
     * @param on the position to place on.
     * @param facing the offset.
     */
    protected void placeBlock(BlockPos on, Direction facing, float[] rotations, int slot)
    {
        if (rotations != null)
        {
            BlockHitResult result =
                    RayTraceUtil.getBlockHitResult(rotations[0], rotations[1]);

            float[] f = RayTraceUtil.hitVecToPlaceVec(on, result.getPos());
            int lastSlot = mc.player.getInventory().selectedSlot;
            boolean sneaking = smartSneak.getValue()
                    && !SpecialBlocks.shouldSneak(on, true);

            if (bypass.getValue()) {
                InventoryUtil.switchToBypass(slot);
            } else {
                InventoryUtil.switchTo(slot);
            }
            if (!sneaking)
            {
                mc.player.networkHandler.sendPacket(
                        new ClientCommandC2SPacket(mc.player,
                                ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }

            NetworkUtil.sendSequenced(seq -> new PlayerInteractBlockC2SPacket(
                            InventoryUtil.getHand(slot),
                            new BlockHitResult(new Vec3d(f[0], f[1], f[2]), facing, on, false),
                            seq));
            mc.player.networkHandler.sendPacket(
                    new HandSwingC2SPacket(InventoryUtil.getHand(slot)));

            if (!sneaking)
            {
                mc.player.networkHandler.sendPacket(
                        new ClientCommandC2SPacket(mc.player,
                                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }

            if (mc.player.getInventory().getStack(InventoryUtil.hotbarToInventory(lastSlot)).getItem() != Items.DIAMOND_PICKAXE) {
                if (bypass.getValue()) {
                    InventoryUtil.switchToBypass(lastSlot);
                } else {
                    InventoryUtil.switchTo(lastSlot);
                }
            }
        }
    }

}
