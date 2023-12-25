package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagPages;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagRotate;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagStage;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.OffsetMode;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

// TODO: thingy that makes crystals fall on us
public class BlockLag extends DisablingModule
{
    protected static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);
    static final ModuleCache<Blink> BLINK =
            Caches.getModule(Blink.class);
    protected final Setting<BlockLagPages> pages =
            register(new EnumSetting<>("Page", BlockLagPages.Offsets));
    // --------------------- OFFSETS --------------------- //
    protected final Setting<OffsetMode> offsetMode =
            register(new EnumSetting<>("Mode", OffsetMode.Smart));
    protected final Setting<BlockLagStage> stage =
            register(new EnumSetting<>("Stage", BlockLagStage.All));
    protected final Setting<Double> vClip =
            register(new NumberSetting<>("V-Clip", -9.0, -20.0, 20.0)); // old one was 256 range
    protected final Setting<Double> minDown =
            register(new NumberSetting<>("Min-Down", 3.0, 0.0, 1337.0));
    protected final Setting<Double> maxDown =
            register(new NumberSetting<>("Max-Down", 10.0, 0.0, 1337.0));
    protected final Setting<Double> minUp =
            register(new NumberSetting<>("Min-Up", 3.0, 0.0, 1337.0));
    protected final Setting<Double> maxUp =
            register(new NumberSetting<>("Max-Up", 10.0, 0.0, 1337.0));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 100, 0, 1000));
    protected final Setting<Boolean> skipZero =
            register(new BooleanSetting("SkipZero", true));
    protected final Setting<Boolean> fallback =
            register(new BooleanSetting("Fallback", true));
    protected final Setting<Boolean> air =
            register(new BooleanSetting("Air", false));
    protected final Setting<Boolean> discrete =
            register(new BooleanSetting("Discrete", true));

    // --------------------- ROTATIONS & MISC --------------------- //
    protected final Setting<BlockLagRotate> rotate =
            register(new EnumSetting<>("Rotate", BlockLagRotate.Packet));
    protected final Setting<Boolean> anvil =
            register(new BooleanSetting("Anvil", false));
    protected final Setting<Boolean> echest =
            register(new BooleanSetting("E-Chest", false));
    protected final Setting<Boolean> beacon =
            register(new BooleanSetting("Beacon", false));
    protected final Setting<Boolean> allowUp =
            register(new BooleanSetting("Allow-Up", false));
    protected final Setting<Boolean> onGround =
            register(new BooleanSetting("OnGround", true));
    protected final Setting<Boolean> conflict =
            register(new BooleanSetting("Conflict", true));
    protected final Setting<Boolean> noVoid =
            register(new BooleanSetting("NoVoid", false));
    protected final Setting<Boolean> evade =
            register(new BooleanSetting("Evade", false));
    protected final Setting<Boolean> freecam =
            register(new BooleanSetting("Freecam", false));
    protected final Setting<Boolean> highBlock =
            register(new BooleanSetting("HighBlock", false));
    protected final Setting<Float> motionAmount =
            register(new NumberSetting<>("Motion-Amount", 60f, 0.1f, 1337.0f))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Boolean> motionNegate =
            register(new BooleanSetting("Motion-Negate", false));
    protected final Setting<Float> negateAmount =
            register(new NumberSetting<>("Negate-Amount", -120f, 0.1f, -1337.0f))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Boolean> useBlink =
            register(new BooleanSetting("UseBlink", true))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Boolean> autoDisableBlink =
            register(new BooleanSetting("AutoDisable", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Integer> blinkDuration =
            register(new NumberSetting<>("Blink-Duration", 650, 0, 5000))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Boolean> useTimer =
            register(new BooleanSetting("MotionTimer", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Float> timerAmount =
            register(new NumberSetting<>("MotionTimer-Speed", 6.0f, 0.1f, 30.0f))
                    .setVisibility(offsetMode.getValue() == OffsetMode.Motion);
    protected final Setting<Boolean> bypass =
            register(new BooleanSetting("Bypass", false));
    protected final Setting<Double> bypassOffset =
            register(new NumberSetting<>("BypassOffset", 0.032, 0.001, 0.1));
    protected final Setting<Boolean> wait =
            register(new BooleanSetting("Wait", true));
    protected final Setting<Boolean> placeDisable =
            register(new BooleanSetting("PlaceDisable", false));
    protected final Setting<Boolean> deltaY =
            register(new BooleanSetting("Delta-Y", true));
    protected final Setting<Float> smartRange =
            register(new NumberSetting<>("Range", 3.0f, 0.0f, 10.0f))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);
    protected  final Setting<Boolean> turnoff =
            register(new BooleanSetting("Auto-Off", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);
    protected  final Setting<Boolean> holeOnly =
            register(new BooleanSetting("OnlyHoles", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);
    protected  final Setting<Boolean> onTeleport =
            register(new BooleanSetting("OnTeleport", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);
    protected  final Setting<Boolean> chorusDisable =
            register(new BooleanSetting("DisableTP", false))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);
    protected final Setting<Integer> smartDelay =
            register(new NumberSetting<>("Smart-Delay", 100, 0, 1000))
                    .setVisibility(offsetMode.getValue() == OffsetMode.SmartNew);

    // --------------------- ATTACK, POP --------------------- //
    protected final Setting<Boolean> attack =
            register(new BooleanSetting("Attack", false));
    protected final Setting<Boolean> instantAttack =
            register(new BooleanSetting("Instant-Attack", false));
    protected final Setting<Boolean> antiWeakness =
            register(new BooleanSetting("AntiWeakness", false));
    protected final Setting<Boolean> attackBefore =
            register(new BooleanSetting("Attack-Before", false));
    protected final Setting<Pop> pop =
            register(new EnumSetting<>("Pop", Pop.None));
    protected final Setting<Integer> popTime =
            register(new NumberSetting<>("Pop-Time", 500, 0, 500));
    protected final Setting<Integer> cooldown =
            register(new NumberSetting<>("Cooldown", 500, 0, 500));

    // --------------- EXPLOSION, VELOCITY, SCALE --------------- //
    protected final Setting<Boolean> scaleExplosion =
            register(new BooleanSetting("Scale-Explosion", false));
    protected final Setting<Boolean> scaleVelocity =
            register(new BooleanSetting("Scale-Velocity", false));
    protected final Setting<Boolean> scaleDown =
            register(new BooleanSetting("Scale-Down", false));
    protected final Setting<Integer> scaleDelay =
            register(new NumberSetting<>("Scale-Delay", 250, 0, 1000));
    protected final Setting<Double> scaleFactor =
            register(new NumberSetting<>("Scale-Factor", 1.0, 0.1, 10.0));


    protected final StopWatch scaleTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected final StopWatch blinkTimer = new StopWatch();
    protected final StopWatch jumpTimer = new StopWatch();
    protected double motionY;
    protected BlockPos startPos;

    // Implemented for mode SmartNew by xyzbtw
    protected final StopWatch delayTimer = new StopWatch();
    public boolean blockTeleporting;
    protected boolean ateChorus = false;

    protected PlayerEntity target;
    protected BlockPos pos;

    public BlockLag()
    {
        super("BlockLag", Category.Movement);
        this.setData(new BlockLagData(this));
        this.listeners.add(new ListenerMotion(this));

        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerTeleport(this));
        this.listeners.add(new ListenerEat(this));

        Bus.EVENT_BUS.register(new ListenerVelocity(this));
        Bus.EVENT_BUS.register(new ListenerExplosion(this));
        Bus.EVENT_BUS.register(new ListenerSpawnObject(this));

        new PageBuilder<>(this, pages)
            .addPage(v -> v == BlockLagPages.Offsets, offsetMode, discrete)
            .addPage(v -> v == BlockLagPages.Misc, rotate, deltaY)
            .addPage(v -> v == BlockLagPages.Attack, attack, cooldown)
            .addPage(v -> v == BlockLagPages.Scale, scaleExplosion, scaleFactor)
//            .addPage(v -> v == BlockLagPages.Bypass, motionAmount, timerAmount)
//            .addPage(v -> v == BlockLagPages.Smart, smartRange, smartDelay)
            .register(Visibilities.VISIBILITY_MANAGER);
    }

    @Override
    protected void onEnable()
    {
        if (mc.world == null || mc.player == null)
            return;


        if(offsetMode.getValue() == OffsetMode.SmartNew)
        {

            if(mc.isInSingleplayer()) {
                Managers.CHAT.sendDeleteMessage("You cannot use BlockLag -> SmartNew in local worlds. Sorry!", getName(), ChatIDs.MODULE);
                this.disable();
            }

            delayTimer.setTime(0);
            target = null;
        }


        timer.setTime(0);

        if(offsetMode.getValue() == OffsetMode.Motion)
        {
            jumpTimer.reset();
            blinkTimer.reset();

            if(jumpTimer.passed(295))
                blinkTimer.reset();

            if (useTimer.getValue())
                Managers.TIMER.setTimer(timerAmount.getValue());
        }

        super.onEnable(); // Ummmmmm this might not be necessary.

        startPos = getPlayerPos();
        if (singlePlayerCheck(startPos))
            this.disable();
    }




    protected void attack(Packet<?> attacking, int slot) {
        if (slot != -1) {
            InventoryUtil.switchTo(slot);
        }

        NetworkUtil.send(attacking);
        Swing.Packet.swing(Hand.MAIN_HAND);
    }

    protected double getY(Entity entity, OffsetMode mode) {
        if (mode == OffsetMode.Constant) {
            double y = entity.getY() + vClip.getValue();
            if (evade.getValue() && Math.abs(y) < 1) {
                y = -1;
            }

            return y;
        }

        double d = getY(entity, minDown.getValue(), maxDown.getValue(), true);
        if (Double.isNaN(d)) {
            d = getY(entity, -minUp.getValue(), -maxUp.getValue(), false);
            if (Double.isNaN(d)) {
                if (fallback.getValue()) {
                    return getY(entity, OffsetMode.Constant);
                }
            }
        }

        return d;
    }

    protected double getY(Entity entity, double min, double max, boolean add) {
        if (min > max && add || max > min && !add) {
            return Double.NaN;
        }

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        boolean air = false;
        double lastOff = 0.0;
        BlockPos last = null;
        for (double off = min;
             add ? off < max : off > max;
            //noinspection ConstantConditions ??? intellij drunk
             off = (add ? ++off : --off))
        {
            BlockPos pos = new BlockPos((int) x, (int) (y - off), (int) z);
            if (noVoid.getValue() && pos.getY() < 0) {
                continue;
            }

            if (skipZero.getValue() && Math.abs(y) < 1) {
                air = false;
                last = pos;
                lastOff = y - off;
                continue;
            }

            BlockState state = mc.world.getBlockState(pos);
            if (!this.air.getValue() && !state.blocksMovement()
                    || state.getBlock() == Blocks.AIR) {
                if (air) {
                    if (add) {
                        return discrete.getValue() ? pos.getY() : y - off;
                    } else {
                        return discrete.getValue() ? last.getY() : lastOff;
                    }
                }

                air = true;
            } else {
                air = false;
            }

            last = pos;
            lastOff = y - off;
        }

        return Double.NaN;
    }

    // TODO: make this smarter!!!!!!!!!!!!!!!!!!!!
    protected double applyScale(double value) {
        if (value < mc.player.getY() && !scaleDown.getValue()
                || !scaleExplosion.getValue() && !scaleVelocity.getValue()
                || scaleTimer.passed(scaleDelay.getValue())
                || motionY == 0.0) {
            return value;
        }

        if (value < mc.player.getY()) {
            value -= (motionY * scaleFactor.getValue());
        } else {
            value += (motionY * scaleFactor.getValue());
        }


        return discrete.getValue() ? Math.floor(value) : value;
    }

    protected BlockPos getPlayerPos() {
        return deltaY.getValue() && Math.abs(mc.player.getVelocity().getY()) > 0.1
                ? new BlockPos(mc.player.getBlockPos())
                : PositionUtil.getPosition(mc.player);
    }

    protected boolean isInsideBlock() {
        double x = mc.player.getX();
        double y = mc.player.getY() + 0.20;
        double z = mc.player.getZ();

        return mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).blocksMovement() || !mc.player.verticalCollision;
    }

    public BlockPos getNearestLagBack() {
        List<BlockPos> positions = new ArrayList<>();
        /*
        for (BlockEntity ent : mc.world.loadedTileEntityList){
            if (ent instanceof EnderChestBlockEntity){
                positions.add(ent.getPos());
            } //TODO: BlockEntity list??
        }

         */
        return positions.size() > 0 ? positions.get(1) : new BlockPos(mc.player.getBlockX(), mc.player.getBlockY() + 1, mc.player.getBlockZ());
    }

    protected boolean singlePlayerCheck(BlockPos pos)
    {
        if (mc.isInSingleplayer())
        {
            @SuppressWarnings("ConstantConditions")
            PlayerEntity player = mc.getServer()
                                    .getPlayerManager()
                                    .getPlayer(mc.player.getUuid());
            //noinspection ConstantConditions
            if (player == null)
            {
                this.disable();
                return true;
            }

            player.getEntityWorld().setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            mc.world.setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            return true;
        }

        return false;
    }

    protected void onDisable(){
        super.onDisable();
        ateChorus = false;
        Managers.TIMER.setTimer(1);

        blinkTimer.reset();
        jumpTimer.reset();

        if(blinkTimer.passed(blinkDuration.getValue()) && autoDisableBlink.getValue())
            BLINK.disable();
    }
}