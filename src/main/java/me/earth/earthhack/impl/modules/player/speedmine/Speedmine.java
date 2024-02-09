package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import me.earth.earthhack.impl.core.ducks.network.IPlayerActionC2SPacket;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.SpeedminePages;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.mine.MineUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

// TODO Tps Sync
// TODO Test around with multiple blocks
// TODO: Rotations reset if Facing becomes invalid.
// TODO: Rewrite
// TODO: ^^ redo this, maybe inspiration from Phobot since this is not very good
public class Speedmine extends Module {

    private static final ModuleCache<AutoMine> AUTO_MINE =
            Caches.getModule(AutoMine.class);
    private static final ModuleCache<AutoTrap> AUTO_TRAP =
            Caches.getModule(AutoTrap.class);

    /* -------------------- Pages ------------------- */
    protected final Setting<SpeedminePages> pages =
            register(new EnumSetting<>("Page", SpeedminePages.Break));

    /* ---------------- Break Settings -------------- */
    protected final Setting<MineMode> mode     =
            register(new EnumSetting<>("Mode", MineMode.Smart));
    public final Setting<Float> limit       =
            register(new NumberSetting<>("Damage", 1.0f, 0.0f, 2.0f))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Float> range       =
            register(new NumberSetting<>("Range", 7.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> normal     =
            register(new BooleanSetting("Normal", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> multiTask =
            register(new BooleanSetting("MultiTask", false));
    protected final Setting<Boolean> rotate    =
            register(new BooleanSetting("Rotate", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Integer> confirm =
            register(new NumberSetting<>("Confirm", 500, 0, 1000))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> limitRotations =
            register(new BooleanSetting("Limit-Rotations", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> oldVer =
            register(new BooleanSetting("1.12", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> newVerEntities =
            register(new BooleanSetting("UnderEntities", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> down =
            register(new BooleanSetting("Down", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> requireBreakSlot      =
            register(new BooleanSetting("RequireBreakSlot", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Bind> breakBind =
            register(new BindSetting("BreakBind", Bind.none()))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> tpsSync =
            register(new BooleanSetting("TpsSync", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> noReset   =
            register(new BooleanSetting("Reset", true));
    protected final Setting<Boolean> resetFastOnAir     =
            register(new BooleanSetting("ResetFastOnAir", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> resetFastOnNonAir     =
            register(new BooleanSetting("ResetFastOnNonAir", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> resetSwap     =
            register(new BooleanSetting("ResetOnItemSwap", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> abortNextTick =
            register(new BooleanSetting("AbortNextTick", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> event     =
            register(new BooleanSetting("Event", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> cancelEvent =
            register(new BooleanSetting("CancelEvent", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> toAir     =
            register(new BooleanSetting("ToAir", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Integer> delay     =
            register(new NumberSetting<>("ClickDelay", 100, 0, 500))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> cancelClick =
            register(new BooleanSetting("CancelClick", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> cancelNormalPackets =
            register(new BooleanSetting("CancelNormalPackets", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> resetAfterPacket =
            register(new BooleanSetting("ResetAfterPacket", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> checkPacket =
            register(new BooleanSetting("CheckPacket", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> swingStop =
            register(new BooleanSetting("Swing-Stop", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Integer> realDelay =
            register(new NumberSetting<>("Delay", 50, 0, 500))
                    .setComplexity(Complexity.Medium);
    public final Setting<Boolean> onGround  =
            register(new BooleanSetting("OnGround", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Integer> tickTime  =
            register(new NumberSetting<>("TickTime", 50, 0, 200))
                    .setComplexity(Complexity.Expert);

    /* ---------------- Swap Settings -------------- */
    protected final Setting<CooldownBypass> cooldownBypass =
            register(new EnumSetting<>("CoolDownBypass", CooldownBypass.None))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> swap      =
            register(new BooleanSetting("SilentSwitch", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> offhandSilent =
            register(new BooleanSetting("OffhandSilent", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> megaSilent =
            register(new BooleanSetting("MegaSilent", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> antiAntiSilentSwitch =
            register(new BooleanSetting("AntiAntiSilentSwitch", false))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Integer> aASSwitchTime =
            register(new NumberSetting<>("AASSwitchTime", 500, 0, 1000))
                    .setComplexity(Complexity.Expert);

    /* ---------------- Crystal Settings -------------- */
    protected final Setting<Boolean> prePlace =
            register(new BooleanSetting("PrePlace", false))
                    .setComplexity(Complexity.Medium);
    public final Setting<Float> prePlaceLimit       =
            register(new NumberSetting<>("PrePlaceLimit", 0.95f, 0.0f, 2.0f))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> placeCrystal =
            register(new BooleanSetting("PlaceCrystal", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> breakCrystal =
            register(new BooleanSetting("BreakCrystal", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> breakInstant =
            register(new BooleanSetting("BreakSpawningCrystals", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Double> crystalRange =
            register(new NumberSetting<>("CrystalRange", 6.0, 0.0, 10.0))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Double> crystalTrace =
            register(new NumberSetting<>("CrystalTrace", 6.0, 0.0, 10.0))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Double> crystalBreakTrace =
            register(new NumberSetting<>("CrystalBreakTrace", 3.0, 0.0, 10.0))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Double> minDmg =
            register(new NumberSetting<>("MinDamage", 10.0, 0.0, 36.0))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Double> maxSelfDmg =
            register(new NumberSetting<>("MaxSelfDamage", 10.0, 0.0, 36.0))
                    .setComplexity(Complexity.Expert);

    protected final Setting<Boolean> offhandPlace =
            register(new BooleanSetting("OffhandPlace", false))
                    .setComplexity(Complexity.Expert);

    /* ---------------- Render Settings -------------- */
    protected final Setting<ESPMode> esp       =
            register(new EnumSetting<>("ESP", ESPMode.Outline))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> display   =
            register(new BooleanSetting("DisplayDamage", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Integer> alpha     =
            register(new NumberSetting<>("BlockAlpha", 100, 0, 255))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Integer> outlineA  =
            register(new NumberSetting<>("OutlineAlpha", 100, 0, 255))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> airFastRender =
            register(new BooleanSetting("NoFastOnAir", true))
                    .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> growRender =
            register(new BooleanSetting("GrowRender", false))
                    .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> smoothenRender =
            register(new BooleanSetting("Smoothen", false))
                    .setComplexity(Complexity.Medium);
    // protected final Setting<Color> pbColor  =
    //         register(new ColorSetting("PB-Color", new Color(0, 255, 0, 240)))
    //                 .setComplexity(Complexity.Expert);
    // protected final Setting<Color> pbOutline  =
    //         register(new ColorSetting("PB-Outline", new Color(0, 255, 0, 120)))
    //                 .setComplexity(Complexity.Expert);

    protected final FastHelper fastHelper = new FastHelper(this);
    public final CrystalHelper crystalHelper = new CrystalHelper(this);
    protected final OngroundHistoryHelper ongroundHistoryHelper =
            new OngroundHistoryHelper();

    /**
     * Damage dealt to block for each hotbarSlot.
     */
    public final float[] damages =
            new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    /**
     * A StopWatch to handle ClickDelay.
     */
    protected final StopWatch timer = new StopWatch();
    /**
     * A StopWatch to handle Resetting after sending a Packet.
     */
    protected final StopWatch resetTimer = new StopWatch();
    protected final StopWatch aASSSwitchTimer = new StopWatch();
    /**
     * The Pos we are currently mining.
     */
    protected BlockPos pos;
    /**
     * The facing we hit the current pos int.
     */
    protected Direction facing;
    /**
     * Cached boundingBox for the currentPos.
     */
    protected Box bb;
    /**
     * Rotations to the current pos.
     */
    protected float[] rotations;
    /**
     * Maximum damage dealt to the current Pos.
     */
    public float maxDamage;
    /**
     * <tt>true</tt> if we sent the STOP_DESTROY packet.
     */
    protected boolean sentPacket;
    /**
     * true if we should send an abort packet
     */
    protected boolean shouldAbort;
    /**
     * true if the module should not send destroy block packets right now
     */
    protected boolean pausing;
    /**
     * timer for delays
     */
    protected final StopWatch delayTimer = new StopWatch();
    /**
     * Packet to send after we limited our rotations.
     */
    protected Packet<?> limitRotationPacket;
    /**
     * Slot for LimitRotations.
     */
    protected int limitRotationSlot = -1;

    public Speedmine() 
    {
        super("Speedmine", Category.Player);
        this.listeners.add(new ListenerDamage(this));
        this.listeners.add(new ListenerReset(this));
        this.listeners.add(new ListenerClick(this));
        // this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerUpdateSelectedSlot(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerSpawnObject(this));
        this.setData(new SpeedMineData(this));
        new PageBuilder<>(this, pages)
                .addPage(p -> p == SpeedminePages.Break, mode, tickTime)
                .addPage(p -> p == SpeedminePages.Swap, swap, aASSwitchTime)
                .addPage(p -> p == SpeedminePages.Crystal, prePlace, offhandPlace)
                .addPage(p -> p == SpeedminePages.Render, esp, smoothenRender)
                .register(Visibilities.VISIBILITY_MANAGER);
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    @Override
    public String getDisplayInfo()
    {
        if (display.getValue()
                && (mode.getValue() == MineMode.Smart
                || mode.getValue() == MineMode.Fast))
        {
            return (maxDamage >= limit.getValue()
                    ? TextColor.GREEN + MathUtil.round(limit.getValue(), 1)
                    : "" + MathUtil.round(maxDamage, 1));
        }

        return mode.getValue().toString();
    }

    /**
     * Sends an ABORT_DESTROY_BLOCK PlayerActionC2SPacket for the
     * current pos and resets the playerController.
     */
    public void abortCurrentPos()
    {
        AUTO_MINE.computeIfPresent(a -> a.addToBlackList(pos));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                pos,
                facing));

        ((IClientPlayerInteractionManager) mc.interactionManager).earthhack$setIsHittingBlock(false);
        ((IClientPlayerInteractionManager) mc.interactionManager).earthhack$setCurBlockDamageMP(0.0f);
        mc.world.setBlockBreakingInfo(this.mc.player.getId(), pos, -1);
        mc.player.resetLastAttackedTicks(); // todo : mc.player.resetCooldown(); is probably different
        reset();
    }

    /**
     * Resets the current pos and all damages dealt to it.
     */
    public void reset()
    {
        pos    = null;
        facing = null;
        bb     = null;
        maxDamage  = 0.0f;
        sentPacket = false;
        limitRotationSlot = -1;
        limitRotationPacket = null;
        fastHelper.reset();
        AUTO_MINE.computeIfPresent(AutoMine::reset);

        for (int i = 0; i < 9; i++)
        {
            damages[i] = 0.0f;
        }
    }

    public void retry() {
        BlockPos cachedPos = getPos();
        Direction facing = RayTraceUtil.getFacing(mc.player, cachedPos, true);
        if (facing == null) {
            ModuleUtil.sendMessage(this, "Mining failure; facing is null.");
            return;
        }

        reset();

        mc.interactionManager.attackBlock(cachedPos, facing);
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                cachedPos,
                facing));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                cachedPos,
                facing));
    }

    /**
     * Returns the current mode.
     *
     * @return a MineMode.
     */
    public MineMode getMode()
    {
        return mode.getValue();
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public StopWatch getTimer()
    {
        return timer;
    }

    public float getRange()
    {
        return range.getValue();
    }

    public int getBlockAlpha() {
        return alpha.getValue();
    }

    public int getOutlineAlpha() {
        return outlineA.getValue();
    }
    public Direction getFacing() {
        return this.facing;
    }
    
    public boolean isPausing() {
        return pausing;
    }
    public void setPausing(boolean pausing) {
        this.pausing = pausing;
    }

    protected boolean sendStopDestroy(BlockPos pos,
                                      Direction facing,
                                      boolean toAir)
    {
        return sendStopDestroy(pos, facing, toAir, true);
    }

    protected boolean sendStopDestroy(BlockPos pos,
                                      Direction facing,
                                      boolean toAir,
                                      boolean withRotations)
    {
        PlayerActionC2SPacket stop  =
                new PlayerActionC2SPacket(
                        PlayerActionC2SPacket
                                .Action
                                .STOP_DESTROY_BLOCK,
                        pos,
                        facing);
        ModuleUtil.sendMessage(this, "Attempting to send STOP_DESTROY_BLOCK packet!");
        ModuleUtil.sendMessage(this, "Parameters: " + pos + ", " + facing);
        try
        {
            if (toAir)
            {
                //noinspection ConstantConditions
                ((IPlayerActionC2SPacket) stop).earthhack$setClientSideBreaking(true);
            }

            if (withRotations
                    && rotate.getValue()
                    && limitRotations.getValue()
                    && !RotationUtil.isLegit(pos, facing))
            {
                limitRotationPacket = stop;
                limitRotationSlot = mc.player.getInventory().selectedSlot;
                return false;
            }

            if (event.getValue())
            {
                mc.player.networkHandler.sendPacket(stop);
            }
            else
            {
                NetworkUtil.sendPacketNoEvent(stop, false);
            }

            if (mode.getValue() == MineMode.Fast)
            {
                fastHelper.sendAbortStart(pos, facing);
            }

            onSendPacket();
        }
        catch (Exception ex)
        {
            ModuleUtil.sendMessage(this, "LOL epic fail happened!!!!!");
            ModuleUtil.sendMessage(this, ex.getMessage());
        }
        return true;
    }

    protected void postSend(boolean toAir)
    {
        if (placeCrystal.getValue())
        {
            AUTO_TRAP.computeIfPresent(autoTrap -> autoTrap.blackList
                    .put(pos, System.currentTimeMillis()));
        }

        if (swingStop.getValue())
        {
            Swing.Packet.swing(Hand.MAIN_HAND);
        }

        if (toAir)
        {
            mc.interactionManager.breakBlock(pos);
        }

        if (breakCrystal.getValue())
        {
            IBlockStateHelper helper = new BlockStateHelper();
            helper.addAir(pos);
            for (Entity crystal : mc.world.getEntities())
            {
                double distance;
                if (crystal instanceof EndCrystalEntity
                        && !EntityUtil.isDead(crystal)
                        && (distance = mc.player.squaredDistanceTo(crystal)) < MathUtil.square(crystalRange.getValue())
                        && (mc.player.canSee(crystal) || distance < MathUtil.square(crystalBreakTrace.getValue())))
                {
                    float selfDamage = DamageUtil.calculate(crystal, mc.player, (ClientWorld) helper);
                    if (selfDamage < EntityUtil.getHealth(mc.player) && selfDamage < maxSelfDmg.getValue())
                    {
                        for (PlayerEntity player : mc.world.getPlayers())
                        {
                            float damage = DamageUtil.calculate(crystal, player, (ClientWorld) helper);
                            if (damage >= minDmg.getValue())
                            {
                                PacketUtil.attack(crystal);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (resetAfterPacket.getValue() && mode.getValue() != MineMode.Fast)
        {
            reset();
        }
    }

    public void forceSend()
    {
        if (pos != null)
        {
            if (mode.getValue() == MineMode.Instant)
            {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing));
                sendStopDestroy(pos, facing, false);
                if (mode.getValue() == MineMode.Instant)
                {
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, facing));
                }
            }
            else if (mode.getValue() == MineMode.Civ)
            {
                sendStopDestroy(pos, facing, false);
            }
        }
    }

    public void tryBreak() {
        int breakSlot;
        if (!pausing && ((breakSlot = findBreakSlot()) != -1 || requireBreakSlot.getValue())) {
            boolean toAir = this.toAir.getValue();
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.getInventory().selectedSlot;
                if (breakSlot != -1) {
                    cooldownBypass.getValue().switchTo(breakSlot);
                }

                PlayerActionC2SPacket packet =
                        new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                                pos,
                                facing);

                if (toAir)
                {
                    //noinspection ConstantConditions
                    ((IPlayerActionC2SPacket) packet)
                            .earthhack$setClientSideBreaking(true);
                }

                NetworkUtil.sendPacketNoEvent(packet, false);
                if (breakSlot != -1) {
                    cooldownBypass.getValue().switchBack(lastSlot, breakSlot);
                }
            });

            if (toAir)
            {
                mc.interactionManager.breakBlock(pos);
            }

            onSendPacket();
        }
    }

    private int findBreakSlot()
    {
        int slot = -1;
        for (int i = 0; i < damages.length; i++)
        {
            if (damages[i] >= limit.getValue()
                    && (slot = i) >= mc.player.getInventory().selectedSlot)
            {
                return slot;
            }
        }

        return slot;
    }

    public void checkReset()
    {
        if (sentPacket
                && resetTimer.passed(confirm.getValue())
                && (mode.getValue() == MineMode.Packet
                || mode.getValue() == MineMode.Smart))
        {
            reset();
        }
    }

    public void onSendPacket()
    {
        ModuleUtil.sendMessage(this, "Yooooo sending the packet didnt fail");
        sentPacket = true;
        resetTimer.reset();
    }

    public void updateDamages()
    {
        ModuleUtil.sendMessage(this, "Updating damages.");
        maxDamage = 0.0f;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            float damage = MineUtil.getDamage(stack, pos, onGround.getValue());
            if (tpsSync.getValue()) {
                damage *= Managers.TPS.getFactor();
            }

            damages[i] = MathUtil.clamp(damages[i] + damage, 0.0f, Float.MAX_VALUE);
            if (damages[i] > maxDamage)
            {
                maxDamage = damages[i];
            }
            ModuleUtil.sendMessage(this, "Updated; Maximum is " + MathUtil.round(maxDamage, 1));
        }
    }

    public int getFastSlot()
    {
        int fastSlot = -1;
        for (int i = 0; i < damages.length; i++)
        {
            if (damages[i] >= limit.getValue())
            {
                fastSlot = i;
                if (i == mc.player.getInventory().selectedSlot)
                {
                    break;
                }
            }
        }

        return fastSlot;
    }

    public void postCrystalPlace(int fastSlot, int lastSlot, boolean swap)
    {
        if (swap)
        {
            cooldownBypass.getValue().switchTo(fastSlot);
        }

        boolean toAir = this.toAir.getValue();
        InventoryUtil.syncItem();
        if (sendStopDestroy(pos, facing, toAir))
        {
            postSend(toAir);
        }

        if (swap)
        {
            cooldownBypass.getValue().switchBack(lastSlot, fastSlot);
        }
    }

    public boolean prePlaceCheck()
    {
        if (prePlace.getValue() && placeCrystal.getValue())
        {
            for (float damage : damages)
            {
                if (damage >= prePlaceLimit.getValue())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
