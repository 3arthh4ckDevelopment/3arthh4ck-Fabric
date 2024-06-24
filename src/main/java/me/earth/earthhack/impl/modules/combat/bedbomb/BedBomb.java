package me.earth.earthhack.impl.modules.combat.bedbomb;

import com.google.common.util.concurrent.AtomicDouble;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// TODO: Rewrite
public class BedBomb extends Module
{
    private final Setting<Boolean> place = register(new BooleanSetting("Place", false));
    private final Setting<Integer> placeDelay = register(new NumberSetting<>("Placedelay", 50, 0, 500));
    private final Setting<Float> placeRange = register(new NumberSetting<>("PlaceRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Boolean> extraPacket = register(new BooleanSetting("InsanePacket", false));
    private final Setting<Boolean> packet = register(new BooleanSetting("Packet", false));

    private final Setting<Boolean> explode = register(new BooleanSetting("Break", true));
    private final Setting<BreakLogic> breakMode = register(new EnumSetting<>("BreakMode", BreakLogic.ALL));
    private final Setting<Integer> breakDelay = register(new NumberSetting<>("Breakdelay", 50, 0, 500));
    private final Setting<Float> breakRange = register(new NumberSetting<>("BreakRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Float> minDamage = register(new NumberSetting<>("MinDamage", 5.0f, 1.0f, 36.0f));
    private final Setting<Float> range = register(new NumberSetting<>("Range", 10.0f, 1.0f, 12.0f));
    private final Setting<Boolean> suicide = register(new BooleanSetting("Suicide", false));
    private final Setting<Boolean> removeTiles = register(new BooleanSetting("RemoveTiles", false));
    private final Setting<Boolean> rotate = register(new BooleanSetting("Rotate", false));
    private final Setting<Boolean> oneDot15 = register(new BooleanSetting("1.15", false));
    private final Setting<Logic> logic = register(new EnumSetting<>("Logic", Logic.BREAKPLACE));

    private final Setting<Boolean> craft = register(new BooleanSetting("Craft", false));
    private final Setting<Boolean> placeCraftingTable = register(new BooleanSetting("PlaceTable", false));
    private final Setting<Boolean> openCraftingTable = register(new BooleanSetting("OpenTable", false));
    private final Setting<Boolean> craftTable = register(new BooleanSetting("CraftTable", false));
    private final Setting<Float> tableRange = register(new NumberSetting<>("TableRange", 6.0f, 1.0f, 10.0f));
    private final Setting<Integer> craftDelay = register(new NumberSetting<>("CraftDelay", 4, 1, 10));
    private final Setting<Integer> tableSlot = register(new NumberSetting<>("TableSlot", 8, 0, 8));

    private final StopWatch breakTimer = new StopWatch();
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch craftTimer = new StopWatch();

    private PlayerEntity target = null;
    private boolean sendRotationPacket = false;

    private final AtomicDouble yaw = new AtomicDouble(-1D);
    private final AtomicDouble pitch = new AtomicDouble(-1D);
    private final AtomicBoolean shouldRotate = new AtomicBoolean(false);

    private MotionUpdateEvent current;
    public static RaycastContext raycastContext;

    private boolean one;
    private boolean two;
    private boolean three;
    private boolean four;
    private boolean five;
    private boolean six;

    private BlockPos maxPos = null;
    private boolean shouldCraft;
    private int craftStage = 0;
    private int bedSlot = -1;

    private BlockPos finalPos;
    private Direction finalFacing;



    public BedBomb()
    {
        super("BedBomb", Category.Combat);
        this.setData(new SimpleData(this, "Quick and dirty Port of the awful old Phobos BedBomb."));
        this.listeners.add(new EventListener<MotionUpdateEvent>(MotionUpdateEvent.class)
        {
            @Override
            public void invoke(MotionUpdateEvent event)
            {
                onUpdateWalkingPlayer(event);
            }
        });
        this.listeners.addAll(new CPacketPlayerListener()
        {
            @Override
            protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }

            @Override
            protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
            {
                BedBomb.this.onPacket(event.getPacket());
            }
        }.getListeners());
    }

    @Override
    protected void onEnable()
    {
        current = null;
        bedSlot = -1;
        sendRotationPacket = false;
        target = null;
        yaw.set(-1D);
        pitch.set(-1D);
        shouldRotate.set(false);
        shouldCraft = false;
        raycastContext = new RaycastContext(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
    }

    public void onPacket(PlayerMoveC2SPacket packet) {
        if (shouldRotate.get()) {
            ((ICPacketPlayer) packet).setYaw((float) this.yaw.get());
            ((ICPacketPlayer) packet).setPitch((float) this.pitch.get());
            shouldRotate.set(false);
        }
    }

    public static int findInventoryWool() {
        return InventoryUtil.findInInventory(s ->
        {
            if (s.getItem() instanceof BlockItem)
            {
                Block block = ((BlockItem) s.getItem()).getBlock();
                return block.getDefaultState().isIn(BlockTags.WOOL);
            }

            return false;
        }, true);
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, Hand hand, Direction direction, boolean packet) {
        if (packet) {
            mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(hand, new BlockHitResult(vec, direction, pos, false), 0));
        } else {
            mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(vec, direction, pos, false));
        }
        mc.player.swingHand(hand.MAIN_HAND);
    }

    public void onUpdateWalkingPlayer(MotionUpdateEvent event) {
        current = event;
        if ((mc.player.getWorld().getRegistryKey() == World.OVERWORLD)) {
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            doBedBomb();

            if (shouldCraft)
            {
                if (mc.currentScreen instanceof CraftingScreen)
                {
                    int woolSlot = findInventoryWool();
                    int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS), true);
                    if (woolSlot == -1 || woodSlot == -1 || woolSlot == -2 || woodSlot == -2)
                    {
                        mc.setScreen(null);
                        mc.currentScreen = null;
                        shouldCraft = false;
                        return;
                    }
                    ChatUtil.sendMessage("Here1");
                    if (craftStage > 1 && !one)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 1, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        one = true;
                    } else if (craftStage > (1 + craftDelay.getValue()) && !two)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 2, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        two = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 2) && !three)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 3, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        three = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 3) && !four)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 4, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        four = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 4) && !five)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 5, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        five = true;
                    } else if (craftStage > (1 + craftDelay.getValue() * 5) && !six)
                    {
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 6, 1, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        recheckBedSlots(woolSlot, woodSlot);
                        mc.interactionManager.clickSlot(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
                        six = true;
                        one = false;
                        two = false;
                        three = false;
                        four = false;
                        five = false;
                        six = false;
                        craftStage = -2;
                        shouldCraft = false;
                    }
                    craftStage++;
                    /*
                    if (craftTimer.passedMs(craftDelay.getValue() * 10)) {
                        mc.interactionManager.clickSlot(1, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
                        shouldCraft = false;
                        craftTimer.reset();
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 9)) {
                        mc.interactionManager.clickSlot(1, InventoryUtil.findEmptyHotbarSlot(), 0, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 8)) {
                        mc.interactionManager.clickSlot(1, 6, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 7)) {
                        mc.interactionManager.clickSlot(1, 5, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 6)) {
                        mc.interactionManager.clickSlot(1, 4, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 5)) {
                        mc.interactionManager.clickSlot(1, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 4)) {
                        mc.interactionManager.clickSlot(1, 3, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 3)) {
                        mc.interactionManager.clickSlot(1, 2, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue() * 2)) {
                        mc.interactionManager.clickSlot(1, 1, 1, SlotActionType.PICKUP, mc.player);
                        return;
                    } else if (craftTimer.passedMs(craftDelay.getValue())) {
                        mc.interactionManager.clickSlot(1, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                        return;
                    }
                    if (lastCraftStage == craftStage) return;
                    switch(craftStage) {
                        case 0:
                            mc.interactionManager.clickSlot(1, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 1:
                            mc.interactionManager.clickSlot(1, 1, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 2:
                            mc.interactionManager.clickSlot(1, 2, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 3:
                            mc.interactionManager.clickSlot(1, 3, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 4:
                            mc.interactionManager.clickSlot(1, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 5:
                            mc.interactionManager.clickSlot(1, 4, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 6:
                            mc.interactionManager.clickSlot(1, 5, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 7:
                            mc.interactionManager.clickSlot(1, 6, 1, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 8:
                            mc.interactionManager.clickSlot(1, InventoryUtil.findEmptyHotbarSlot(), 0, SlotActionType.PICKUP, mc.player);
                            incrementCraftStage();
                            break;
                        case 9:
                            mc.interactionManager.clickSlot(1, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
                            incrementCraftStage();
                            shouldCraft = false;
                            break;
                    }*/
                }
            }
        } else if (event.getStage() == Stage.POST && finalPos != null) {
            Vec3d hitVec = new Vec3d(finalPos.down().getX(), finalPos.down().getY(), finalPos.down().getZ()).add(0.5, 0.5, 0.5).add(new Vec3d(finalFacing.getOpposite().getVector().getX(), finalFacing.getOpposite().getVector().getY(), finalFacing.getOpposite().getVector().getZ()).multiply(0.5)); //ökjlsdhblknsö
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, true));
            InventoryUtil.switchTo(bedSlot);
            rightClickBlock(finalPos.down(), hitVec, bedSlot == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND, Direction.UP, packet.getValue());
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, false));
            placeTimer.reset();
            finalPos = null;
        }
    }

    public void recheckBedSlots(int woolSlot, int woodSlot) {
        for (int i = 1; i <= 3; i++) {
            if (mc.player.getInventory().getStack(i) == ItemStack.EMPTY) {
                mc.interactionManager.clickSlot(1, woolSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(1, i, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(1, woolSlot, 0, SlotActionType.PICKUP, mc.player);
            }
        }
        for (int i = 4; i <= 6; i++) {
            if (mc.player.getInventory().getStack(i) == ItemStack.EMPTY) {
                mc.interactionManager.clickSlot(1, woodSlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(1, i, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(1, woodSlot, 0, SlotActionType.PICKUP, mc.player);
            }
        }
    }

    public void incrementCraftStage() {
        if (craftTimer.passed(craftDelay.getValue())) {
            craftStage++;
            if (craftStage > 9) {
                craftStage = 0;
            }
            craftTimer.reset();
        }
    }

    private void doBedBomb() {
        switch (logic.getValue()) {
            case BREAKPLACE:
                mapBeds();
                breakBeds();
                placeBeds();
                break;
            case PLACEBREAK:
                mapBeds();
                placeBeds();
                breakBeds();
                break;
        }
    }

    private void breakBeds() {
        if (explode.getValue() && breakTimer.passed(breakDelay.getValue())) {
            if (breakMode.getValue() == BreakLogic.CALC) {
                if (maxPos != null) {
                    //mc.getNetworkHandler().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), Hand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                    Vec3d hitVec = new Vec3d(maxPos.getX(), maxPos.getY(), maxPos.getZ()).add(0.5, 0.5, 0.5);
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }
                    VoxelShape voxelShape = raycastContext.getBlockShape(mc.world.getBlockState(maxPos), mc.world, maxPos);
                    BlockHitResult result = mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(maxPos.getX() + .5, maxPos.getY() - .5d, maxPos.getZ() + .5), maxPos, voxelShape, getState(maxPos));
                    Direction facing = (result == null || result.getSide() == null) ? Direction.UP : result.getSide();
                    //if (mc.player.isSneaking()) {
                    rightClickBlock(maxPos, hitVec, Hand.MAIN_HAND, facing, true);
                    //mc.getNetworkHandler().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    //}
                    breakTimer.reset();
                }
            } else {
                for (BlockEntity entityBed : getBlockEntities()) {
                    if (!(entityBed instanceof BedBlockEntity)) continue;
                    if (mc.player.squaredDistanceTo(entityBed.getPos().getX(), entityBed.getPos().getY(), entityBed.getPos().getZ()) > MathUtil.square(breakRange.getValue())) continue;
                    //mc.getNetworkHandler().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    Vec3d hitVec = new Vec3d(entityBed.getPos().getX(), entityBed.getPos().getY(), entityBed.getPos().getZ()).add(0.5, 0.5, 0.5);
                    //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), Hand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                    float[] rotations = RotationUtil.getLegitRotations(hitVec);
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }
                    VoxelShape voxelShape = raycastContext.getBlockShape(mc.world.getBlockState(entityBed.getPos()), mc.world, entityBed.getPos());
                    BlockHitResult result = mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(entityBed.getPos().getX() + .5, entityBed.getPos().getY() - .5d, entityBed.getPos().getZ() + .5), entityBed.getPos(), voxelShape, getState(entityBed.getPos()));
                    //if (mc.player.isSneaking()) {
                    Direction facing = (result == null || result.getSide() == null) ? Direction.UP : result.getSide();
                    rightClickBlock(entityBed.getPos(), hitVec, Hand.MAIN_HAND, facing, true);
                    //mc.getNetworkHandler().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    //}
                    breakTimer.reset();
                }
            }
        }
    }

    public static boolean cantTakeDamage(boolean suicide) {
        return mc.player.getAbilities().creativeMode || suicide;
    }

    private void mapBeds() {
        maxPos = null;
        float maxDamage = 0.5f;

        if (removeTiles.getValue()) {
            List<BedData> removedBlocks = new ArrayList<>();
            for (BlockEntity tile : getBlockEntities()) {
                if (tile instanceof BedBlockEntity) {
                    BedBlockEntity bed = (BedBlockEntity) tile;
                    BedData data = new BedData(tile.getPos(), mc.world.getBlockState(tile.getPos()), bed);
                    removedBlocks.add(data);
                }
            }

            for (BedData data : removedBlocks) {
                mc.world.setBlockState(data.getPos(), Blocks.AIR.getDefaultState());
                //data.getEntity().onChunkUnload();
            }

            for (BedData data : removedBlocks) {
                BlockPos pos = data.getPos();
                if (mc.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= MathUtil.square(breakRange.getValue())) {
                    float selfDamage = DamageUtil.calculate(pos, mc.player);
                    if (selfDamage + 1.0 < EntityUtil.getHealth(mc.player) || cantTakeDamage(suicide.getValue())) {
                        for (PlayerEntity player : mc.world.getPlayers()) {
                            if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < MathUtil.square((range.getValue())) && EntityUtil.isValid(player, (range.getValue() + breakRange.getValue()))) {
                                float damage = DamageUtil.calculate(pos, player);
                                if (damage > selfDamage || (damage > minDamage.getValue() && cantTakeDamage(suicide.getValue())) || damage > EntityUtil.getHealth(player)) {
                                    if (damage > maxDamage) {
                                        maxDamage = damage;
                                        maxPos = pos;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (BedData data : removedBlocks) {
                //mc.world.addTileEntity(data.getEntity());
                mc.world.setBlockState(data.getPos(), data.getState());
            }
        } else {
            for(BlockEntity tile : getBlockEntities()) {
                if (tile instanceof BedBlockEntity) {
                    BedBlockEntity bed = (BedBlockEntity) tile;
                    BlockPos pos = bed.getPos();
                    if (mc.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= MathUtil.square(breakRange.getValue())) {
                        float selfDamage = DamageUtil.calculate(pos, mc.player);
                        //added 1.0 for some more safety
                        if (selfDamage + 1.0 < EntityUtil.getHealth(mc.player) || cantTakeDamage(suicide.getValue())) {
                            for (PlayerEntity player : mc.world.getPlayers()) {
                                if (player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < MathUtil.square((range.getValue())) && EntityUtil.isValid(player, (range.getValue() + breakRange.getValue()))) {
                                    float damage = DamageUtil.calculate(pos, player);
                                    if (damage > selfDamage || (damage > minDamage.getValue() && cantTakeDamage(suicide.getValue())) || damage > EntityUtil.getHealth(player)) {
                                        if (damage > maxDamage) {
                                            maxDamage = damage;
                                            maxPos = pos;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    //Lol thanks thunderhack but seriously just do mixin into Chunkmanager + accesswidner, but to lazy
    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();
        for (WorldChunk chunk : getLoadedChunks())
            list.addAll(chunk.getBlockEntities().values());

        return list;
    }

    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> chunks = new ArrayList<>();
        int viewDist = mc.options.getViewDistance().getValue();
        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);

                if (chunk != null) chunks.add(chunk);
            }
        }
        return chunks;
    }

    private void placeBeds() {
        if (place.getValue() && placeTimer.passed(placeDelay.getValue()) && maxPos == null) {
            bedSlot = findBedSlot();
            if (bedSlot == -1) {
                if (mc.player.getOffHandStack().getItem().getDefaultStack().isIn(ItemTags.BEDS)) {
                    bedSlot = -2;
                } else {
                    if (craft.getValue() && !shouldCraft
                            && EntityUtil.getClosestClientEnemy(
                                    CollectionUtil.convertElements(mc.world.getPlayers(), PlayerEntity.class)) != null)
                    {
                        doBedCraft();
                    }
                    return;
                }
            }

            target = EntityUtil.getClosestClientEnemy(CollectionUtil.convertElements(mc.world.getPlayers(), PlayerEntity.class));
            if (target != null && target.squaredDistanceTo(mc.player) < 49) {
                //Silly
                BlockPos targetPos = new BlockPos(new Vec3i((int) target.getX(), (int) target.getY(), (int) target.getZ()));
                placeBed(targetPos, true);
                if (craft.getValue()) {
                    doBedCraft();
                }
            }
        }
    }

    private void placeBed(BlockPos pos, boolean firstCheck) {
        if (mc.world.getBlockState(pos).getBlock() instanceof BedBlock) {
            return;
        }

        float damage = DamageUtil.calculate(pos, mc.player);
        if (damage > EntityUtil.getHealth(mc.player) + 0.5) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        if (!mc.world.getBlockState(pos).isReplaceable()) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        List<BlockPos> positions = new ArrayList<>();
        Map<BlockPos, Direction> facings = new HashMap<>();
        for (Direction facing : Direction.values()) {
            if (facing == Direction.DOWN || facing == Direction.UP) {
                continue;
            }

            BlockPos position = pos.offset(facing);
            if (mc.player.squaredDistanceTo(position.getX(), position.getY(), position.getZ()) <= MathUtil.square(placeRange.getValue()) && mc.world.getBlockState(position).isReplaceable() && !mc.world.getBlockState(position.down()).isReplaceable()) {
                positions.add(position);
                facings.put(position, facing.getOpposite());
            }
        }

        if (positions.isEmpty()) {
            if (firstCheck && oneDot15.getValue()) {
                placeBed(pos.up(), false);
            }
            return;
        }

        positions.sort(Comparator.comparingDouble(pos2 -> mc.player.squaredDistanceTo(pos2.getX(), pos.getY(), pos.getZ())));
        finalPos = positions.get(0);
        finalFacing = facings.get(finalPos);
        float[] rotation = simpleFacing(finalFacing);
        if (!sendRotationPacket && extraPacket.getValue()) {
            faceYawAndPitch(rotation[0], rotation[1]);
            sendRotationPacket = true;
        }

        yaw.set(rotation[0]);
        pitch.set(rotation[1]);
        shouldRotate.set(true);

        if (current != null)
        {
            current.setYaw(rotation[0]);
            current.setPitch(rotation[1]);
        }
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.onGround));
    }

    public static float[] simpleFacing(Direction facing) {
        switch(facing) {
            case DOWN:
                return new float[]{mc.player.yaw, 90.0f};
            case UP:
                return new float[]{mc.player.yaw, -90.0f};
            case NORTH:
                return new float[]{180.0f, 0.0f};
            case SOUTH:
                return new float[]{0.0f, 0.0f};
            case WEST:
                return new float[]{90.0f, 0.0f};
            default:
                return new float[]{270.0f, 0.0f};
        }
    }

    @Override
    public String getDisplayInfo() {
        if (target != null) {
            return target.getName().getString();
        }
        return null;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getBlockSphere(float breakRange, Class<?> clazz) {
        List<BlockPos> positions = new ArrayList<>();
        positions.addAll(getSphere(mc.player.getBlockPos(), breakRange, (int)breakRange, false, true, 0).stream().filter(pos -> clazz.isInstance(mc.world.getBlockState(pos).getBlock())).collect(Collectors.toList()));
        return positions;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        VoxelShape voxelShape = raycastContext.getBlockShape(mc.world.getBlockState(pos), mc.world, pos);
        return !shouldCheck || mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + (double)mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), pos, voxelShape, getState(pos)) == null;
    }

    public static List<Direction> getPossibleSides(BlockPos pos) {
        List<Direction> facings = new ArrayList<>();
        if (mc.world == null || pos == null) {
            return facings;
        }

        for (Direction side : Direction.values()) {
            BlockPos neighbour = pos.offset(side);
            BlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().getDefaultState().isSolid()) {
                if (!blockState.isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).getDefaultState().isSolid();
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static BlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }


    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof AirBlock) && !(block == Blocks.WATER) && !(block == Blocks.LAVA) && !(block instanceof TallPlantBlock) && !(block instanceof FireBlock) && !(block instanceof DeadBushBlock) && !(block instanceof SnowBlock)) {
            return 0;
        }

        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }

        if (entityCheck) {
            Iterable<Entity> entities = mc.world.getOtherEntities(null, new Box(pos),
                    entity -> !(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity));
            for (Entity entity : entities) {
                return 1;
            }
        }

        for(Direction side : getPossibleSides(pos)) {
            if (canBeClicked(pos.offset(side))) {
                return 3;
            }
        }

        return 2;
    }

    public static Direction getFirstFacing(BlockPos pos) {
        for(Direction facing : getPossibleSides(pos)) {
            return facing;
        }
        return null;
    }

    public static boolean placeBlock(BlockPos pos, Hand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        Direction side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }

        BlockPos neighbour = pos.offset(side);
        Direction opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour.getX(), neighbour.getY(), neighbour.getZ()).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getUnitVector()).multiply(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        if (!mc.player.isSneaking() && (SpecialBlocks.BAD_BLOCKS.contains(neighbourBlock) || SpecialBlocks.SHULKERS.contains(neighbourBlock))) {
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, true));
            mc.player.setSneaking(true);
            sneaking = true;
        }

        if (rotate) {
            faceVector(hitVec, true);
        }

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingHand(Hand.MAIN_HAND);
        return sneaking || isSneaking;
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = RotationUtil.getLegitRotations(vec);

        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], normalizeAngle ? ((int) rotations[1] % 360 + 360) % 360 : rotations[1], mc.player.onGround));
    }

    public void doBedCraft() {
        int woolSlot = findInventoryWool();
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS), true);
        if (woolSlot == -1 || woodSlot == -1) {
            if (mc.currentScreen instanceof CraftingScreen) {
                mc.setScreen(null);
                mc.currentScreen = null;
            }
            return;
        }
        if (placeCraftingTable.getValue() && getBlockSphere(tableRange.getValue() - 1, CraftingTableBlock.class).isEmpty()) {
            List<BlockPos> targets = getSphere(mc.player.getBlockPos(), tableRange.getValue(), tableRange.getValue().intValue(), false, true, 0)
                    .stream()
                    .filter(pos -> isPositionPlaceable(pos, false) == 3)
                    .sorted(Comparator.comparingInt(pos -> -safety(pos)))
                    .collect(Collectors.toList());
            if (!targets.isEmpty()) {
                BlockPos target = targets.get(0);
                int tableSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS), true);
                if (tableSlot != -1) {
                    mc.player.getInventory().selectedSlot = tableSlot;
                    /*float[] rotations = RotationUtil.getLegitRotations(new Vec3d(target));
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }*/
                    placeBlock(target, Hand.MAIN_HAND, rotate.getValue(), true, false);
                } else {
                    if (craftTable.getValue()) {
                        craftTable();
                    }
                    tableSlot = InventoryUtil.findInHotbar(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS));
                    if (tableSlot != -1 && tableSlot != -2) {
                        mc.player.getInventory().selectedSlot = tableSlot;
                        placeBlock(target, Hand.MAIN_HAND, rotate.getValue(), true, false);
                    }
                    /*float[] rotations = RotationUtil.getLegitRotations(new Vec3d(target));
                    yaw.set(rotations[0]);
                    if (rotate.getValue()) {
                        shouldRotate.set(true);
                        pitch.set(rotations[1]);
                    }*/
                }
            }
        }
        if (openCraftingTable.getValue()) {
            List<BlockPos> tables = getBlockSphere(tableRange.getValue(), CraftingTableBlock.class);
            tables.sort(Comparator.comparingDouble(pos -> mc.player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())));
            if (!tables.isEmpty() && !(mc.currentScreen instanceof CraftingScreen)) {
                BlockPos target = tables.get(0);
                mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, false));
                //BlockUtil.rightClickBlock(target, tableRange.getValue(), rotate.getValue() && !place.getValue(), Hand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                if (mc.player.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) > MathUtil.square(breakRange.getValue())) return;
                //mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, false));
                Vec3d hitVec = new Vec3d(target.getX(), target.getY(), target.getZ()); // .add(0.5, 0.5, 0.5);
                //BlockUtil.rightClickBlockLegit(maxPos, range.getValue(), rotate.getValue() && !place.getValue(), Hand.MAIN_HAND, yaw, pitch, shouldRotate, true);
                float[] rotations = RotationUtil.getLegitRotations(hitVec);
                yaw.set(rotations[0]);
                if (rotate.getValue()) {
                    shouldRotate.set(true);
                    pitch.set(rotations[1]);
                }
                VoxelShape voxelShape = raycastContext.getBlockShape(mc.world.getBlockState(mc.player.getBlockPos()), mc.world, mc.player.getBlockPos());
                BlockHitResult result = mc.world.raycastBlock(new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ()), new Vec3d(target.getX() + .5, target.getY() - .5d, target.getZ() + .5), mc.player.getBlockPos(), voxelShape, getState(mc.player.getBlockPos()));
                //if (mc.player.isSneaking()) {
                Direction facing = (result == null || result.getSide() == null) ? Direction.UP : result.getSide();
                rightClickBlock(target, hitVec, Hand.MAIN_HAND, facing, true);
                //mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, true));
                //}
                breakTimer.reset();
                if (mc.player.isSneaking()) {
                    mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.sidewaysSpeed, mc.player.forwardSpeed, false, true));
                }
            }
            shouldCraft = mc.currentScreen instanceof CraftingScreen;
            craftStage = 0;
            craftTimer.reset();
            /*if (mc.currentScreen instanceof GuiCrafting) {
                mc.player.connection.sendPacket(new CPacketPlaceRecipe(mc.player.openContainer.windowId, CraftingManager.getRecipe(new ResourceLocation("white_bed")), true));
                mc.playerController.windowClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
            }*/
        }
    }

    public void craftTable() {
        int woodSlot = InventoryUtil.findInInventory(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS), true);
        if (woodSlot != -1) {
            mc.interactionManager.clickSlot(0, woodSlot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 1, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 2, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 3, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 4, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
            int table = InventoryUtil.findInInventory(s -> s.getItem() instanceof BlockItem && ((BlockItem) s.getItem()).getBlock().getDefaultState().isIn(BlockTags.PLANKS), true);
            if (table != -1) {
                mc.interactionManager.clickSlot(0, table, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, tableSlot.getValue(), 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, table, 0, SlotActionType.PICKUP, mc.player);
            }
        }
    }

    private int findBedSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack == ItemStack.EMPTY) {
                continue;
            }

            if (stack.getItem().getDefaultStack().isIn(ItemTags.BEDS)) {
                return i;
            }
        }
        return -1;
    }

    public static class BedData {

        private final BlockPos pos;
        private final BlockState state;
        private final BedBlockEntity entity;

        public BedData(BlockPos pos, BlockState state, BedBlockEntity bed) {
            this.pos = pos;
            this.state = state;
            this.entity = bed;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockState getState() {
            return state;
        }

        public BedBlockEntity getEntity() {
            return entity;
        }
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        for(Direction facing : Direction.values()) {
            if (!mc.world.getBlockState(pos.offset(facing)).isReplaceable()) {
                safety++;
            }
        }
        return safety;
    }

    public enum Logic {
        BREAKPLACE,
        PLACEBREAK
    }

    public enum BreakLogic {
        ALL,
        CALC
    }
}