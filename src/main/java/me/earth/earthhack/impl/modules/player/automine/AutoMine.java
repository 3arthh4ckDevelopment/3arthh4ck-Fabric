package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.modules.player.automine.util.BigConstellation;
import me.earth.earthhack.impl.modules.player.automine.util.IAutomine;
import me.earth.earthhack.impl.modules.player.automine.util.IConstellation;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@SuppressWarnings("ConstantConditions")
public class AutoMine extends BlockAddingModule implements IAutomine
{
    private static final ModuleCache<Speedmine> SPEED_MINE =
        Caches.getModule(Speedmine.class);

    protected final Setting<AutoMineMode> mode =
        register(new EnumSetting<>("Mode", AutoMineMode.Combat));
    protected final Setting<Float> range =
        register(new NumberSetting<>("Range", 6.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> head =
        register(new BooleanSetting("Head", false));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", false));
    protected final Setting<Integer> rotateLimit =
        register(new NumberSetting<>("Rotation-LimitComp", 1800, 0, 2200));
    protected final Setting<Integer> maxY =
            register(new NumberSetting<>("Max-Y", 256, 0, 320));
    protected final Setting<Boolean> self =
        register(new BooleanSetting("Self", true));
    protected final Setting<Boolean> prioSelf =
        register(new BooleanSetting("Prio-SelfUntrap", true));
    protected final Setting<Boolean> prioSelfWithStep =
        register(new BooleanSetting("PrioSelfWithStep", false));
    public final Setting<Boolean> untrapCheck =
        register(new BooleanSetting("UntrapCheck", false));
    protected final Setting<Boolean> constellationCheck =
        register(new BooleanSetting("ConstCheck", true));
    protected final Setting<Boolean> improve =
        register(new BooleanSetting("Improve", false));
    protected final Setting<Boolean> improveInvalid =
        register(new BooleanSetting("ImproveInvalid", false));
    protected final Setting<Integer> delay =
        register(new NumberSetting<>("Delay", 100, 0, 10000));
    protected final Setting<Boolean> newV =
        register(new BooleanSetting("1.13+", false));
    protected final Setting<Boolean> newVEntities =
        register(new BooleanSetting("1.13-Entities", false));
    protected final Setting<Boolean> checkCurrent =
        register(new BooleanSetting("CheckCurrent", true));
    protected final Setting<Boolean> mineL =
        register(new BooleanSetting("Mine-L", false));
    protected final Setting<Integer> offset =
        register(new NumberSetting<>("Reset-Offset", 0, 0, 1000));
    protected final Setting<Boolean> shouldBlackList =
        register(new BooleanSetting("BlackList", true));
    protected final Setting<Integer> blackListFor =
        register(new NumberSetting<>("Blacklist-For", 120, 0, 3600));
    protected final Setting<Boolean> checkTrace =
        register(new BooleanSetting("Check-Range", false));
    protected final Setting<Float> placeRange =
        register(new NumberSetting<>("PlaceRange", 6.0f, 0.1f, 100.0f));
    protected final Setting<Float> placeTrace =
        register(new NumberSetting<>("PlaceTrace", 6.0f, 0.1f, 100.0f));
    protected final Setting<Float> breakTrace =
        register(new NumberSetting<>("BreakTrace", 3.5f, 0.1f, 100.0f));
    protected final Setting<Boolean> crystal =
        register(new BooleanSetting("Crystal", false));
    protected final Setting<Boolean> selfEchestMine =
        register(new BooleanSetting("Self-EchestBurrow-Mine", false));
    protected final Setting<Boolean> mineBurrow =
        register(new BooleanSetting("Mine-Burrow", false));
    protected final Setting<Boolean> checkPlayerState =
        register(new BooleanSetting("CheckPlayerState", true));
    protected final Setting<Boolean> resetIfNotValid =
        register(new BooleanSetting("Reset-Invalid", false));
    protected final Setting<Boolean> terrain =
        register(new BooleanSetting("Terrain", false));
    protected final Setting<Boolean> obbyPositions =
        register(new BooleanSetting("ObbyPositions", false));
    protected final Setting<Boolean> mineObby =
        register(new BooleanSetting("MineObby", false));
    protected final Setting<Boolean> closestPlayer =
        register(new BooleanSetting("ClosestPlayer", true));
    protected final Setting<Boolean> improveCalcs =
        register(new BooleanSetting("ImproveCalcs", false));
    public final Setting<Boolean> extraBurrowCheck =
        register(new BooleanSetting("ExtraBurrowCheck", false));
    public final Setting<Float> minDmg =
        register(new NumberSetting<>("MinDamage", 6.0f, 0.1f, 100.0f));
    public final Setting<Float> maxSelfDmg =
        register(new NumberSetting<>("MaxSelfDmg", 10.0f, 0.1f, 100.0f));
    public final Setting<Boolean> damageCheck =
        register(new BooleanSetting("DamageCheck", false));
    public final Setting<Boolean> selfDmgCheck =
        register(new BooleanSetting("SelfDmgCheck", false));
    protected final Setting<Integer> terrainDelay =
        register(new NumberSetting<>("TerrainDelay", 500, 0, 10000));
    protected final Setting<Boolean> suicide =
        register(new BooleanSetting("Suicide", false));
    protected final Setting<Boolean> echest =
        register(new BooleanSetting("Echests", false));
    protected final Setting<Float> echestRange =
        register(new NumberSetting<>("Echest-Range", 3.0f, 0.1f, 100.0f));
    protected final Setting<Boolean> shulkers =
            register(new BooleanSetting("Shulkers", true));
    protected final Setting<Float> shulkersRange =
            register(new NumberSetting<>("Shulkers-range", 3.0f, 0.1f, 100.0f));
    protected final Setting<Integer> maxTime =
        register(new NumberSetting<>("MaxTime", 20000, 0, 60000));
    protected final Setting<Boolean> checkCrystalDownTime =
        register(new BooleanSetting("CheckCrystalDownTime", false));
    protected final Setting<Integer> downTime =
        register(new NumberSetting<>("AutoCrystalDownTime", 500, 0, 5000));
    public final Setting<Boolean> multiBreakCheck =
        register(new BooleanSetting("MultiBreakCheck", true));
    protected final Setting<Boolean> disableOnNoSpeedmine =
        register(new BooleanSetting("DisableOnBadSpeedmine", true));
    protected final Setting<Boolean> checkEntities =
        register(new BooleanSetting("CheckEntities", false));
    public final Setting<Boolean> speedmineCrystalDamageCheck =
        register(new BooleanSetting("SpeedmineCrystalDamageCheck", false));
    protected final Setting<Boolean> noSelfMine =
        register(new BooleanSetting("NoSelfMine", false));
    protected final Setting<Boolean> antiStuckComp =
        register(new BooleanSetting("AntiStuckCompatibility", false));
    protected final Setting<Boolean> resetOnPacket =
        register(new BooleanSetting("ResetOnPacket", false));
    public final Setting<Boolean> dependOnSMCheck =
        register(new BooleanSetting("DependOnSMCheck", false));

    protected final Map<BlockPos, Long> blackList = new HashMap<>();
    protected final StopWatch constellationTimer = new StopWatch();
    protected final StopWatch terrainTimer = new StopWatch();
    protected final StopWatch downTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();
    protected final StopWatch rotationTimer = new StopWatch();
    protected IConstellation constellation;
    protected Future<?> future;
    protected boolean attacking;
    protected float[] rotations;
    protected Direction facing;
    protected BlockPos current;
    protected BlockPos last;

    public AutoMine()
    {
        super("AutoMine",
              Category.Player,
              s -> "White/Blacklist the mining of " + s.getName() + " blocks.");
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerBlockChange(this));
        // TODO: this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerPlace(this));
        this.listeners.add(new ListenerMotion(this));
        this.listType.setValue(ListType.BlackList);
        this.setData(new AutoMineData(this));

        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class, event -> {
            PlayerActionC2SPacket packet;

            if(event.getPacket() instanceof PlayerActionC2SPacket) {
                packet = (PlayerActionC2SPacket) event.getPacket();

                if(current == packet.getPos())
                    return;

                if (packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
                    if (mode.getValue() == AutoMineMode.Compatibility
                            && antiStuckComp.getValue())
                    {
                        if(current != null) {
                            current = packet.getPos();
                        }
                    }
                }
            }
        }));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    public void onDisable()
    {
        reset(true);
        blackList.clear();
    }

    public AutoMineMode getMode()
    {
        return mode.getValue();
    }

    public void addToBlackList(BlockPos pos)
    {
        if (shouldBlackList.getValue())
        {
            blackList.put(pos, System.currentTimeMillis());
        }
    }

    public void reset()
    {
        this.reset(false);
    }

    public void reset(boolean hard)
    {
        if (!hard && this.constellation instanceof BigConstellation)
        {
            return;
        }

        if (!attacking)
        {
            if (this.future != null)
            {
                future.cancel(true);
                this.future = null;
            }

            constellation = null;
            current = null;
            if (offset.getValue() != 0)
            {
                timer.setTime(System.currentTimeMillis() + offset.getValue());
            }
        }
    }

    protected boolean checkCrystalPos(BlockPos pos)
    {
        if (checkTrace.getValue())
        {
            return BlockUtil.isCrystalPosInRange(pos,
                                                 placeRange.getValue(),
                                                 placeTrace.getValue(),
                                                 breakTrace.getValue())
                && BlockUtil.canPlaceCrystal(pos, true, newV.getValue(),
                            CollectionUtil.asList(mc.world.getEntities()), newVEntities.getValue(), 0);
        }

        return BlockUtil.canPlaceCrystal(pos, true, newV.getValue(),
                            CollectionUtil.asList(mc.world.getEntities()), newVEntities.getValue(), 0);
    }

    @Override
    public boolean isValid(BlockState state)
    {
        return super.isValid(state.getBlock().getName().getString());
    }

    @Override
    public void offer(IConstellation constellation)
    {
        if (this.constellation != null && this.constellation.cantBeImproved()
            || mc.player == null
            || mc.world == null)
        {
            return;
        }

        if (this.future != null)
        {
            future.cancel(true);
            this.future = null;
        }

        this.constellation = constellation;
        this.constellationTimer.reset();
    }

    @Override
    public void attackPos(BlockPos pos)
    {
        facing = RayTraceUtil.getFacing(
            RotationUtil.getRotationPlayer(), pos, true);
        assert facing != null;
        this.current = pos;
        if (mode.getValue() == AutoMineMode.Compatibility) {
            rotationTimer.reset();
            mc.interactionManager.attackBlock(pos, facing);
            this.attacking = true;
        } else {
            SPEED_MINE.get().getTimer().setTime(0);
            // Prevents Reset from getting called by Speedmine.
            this.attacking = true;
            mc.interactionManager.attackBlock(pos, facing);
            this.attacking = false;
            this.timer.reset();
        }
    }

    @Override
    public void setCurrent(BlockPos pos)
    {
        this.current = pos;
    }

    @Override
    public BlockPos getCurrent()
    {
        return current;
    }

    @Override
    public void setFuture(Future<?> future)
    {
        this.future = future;
    }

    @Override
    public float getMinDmg()
    {
        return minDmg.getValue();
    }

    @Override
    public float getMaxSelfDmg()
    {
        return maxSelfDmg.getValue();
    }

    @Override
    public double getBreakTrace()
    {
        return breakTrace.getValue();
    }

    @Override
    public boolean getNewVEntities()
    {
        return newVEntities.getValue();
    }

    @Override
    public boolean shouldMineObby()
    {
        return mineObby.getValue();
    }

    @Override
    public boolean isSuicide()
    {
        return suicide.getValue();
    }

    @Override
    public boolean canBigCalcsBeImproved()
    {
        return improveCalcs.getValue();
    }

    public boolean isValidCrystalPos(BlockPos pos)
    {
        return isValidCrystalPos(pos, false);
    }

    public boolean isValidCrystalPos(BlockPos pos, boolean airCheck)
    {
        BlockState state = mc.world.getBlockState(pos);
        boolean isValidBase =
            airCheck
                && state.getBlock() == Blocks.AIR
                && mc.world.getEntitiesByClass(Entity.class,
                                                  new Box(pos), e -> true)
                           .stream()
                           .noneMatch(e -> /*e.preventEntitySpawning
                                        &&*/ !(e instanceof ItemEntity)
                                        && !(e instanceof EndCrystalEntity))
            || state.getBlock() == Blocks.OBSIDIAN
            || state.getBlock() == Blocks.BEDROCK;

        return isValidBase
            && BlockUtil.checkBoost(
                pos, true, newV.getValue(),
                checkEntities.getValue()
                    ? CollectionUtil.asList(mc.world.getEntities())
                    : Collections.emptyList(),
                newVEntities.getValue(), 0L)
            && BlockUtil.isCrystalPosInRange(
                pos, placeRange.getValue(), placeTrace.getValue(),
                breakTrace.getValue());
    }

}
