package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;
import java.util.Objects;

public class Speed extends Module
{
    private static final ModuleCache<Step> STEP
            = Caches.getModule(Step.class);
    protected final ModuleCache<LongJump> LONG_JUMP = Caches.getModule(LongJump.class);

    protected final Setting<SpeedMode> mode  =
            register(new EnumSetting<>("Mode", SpeedMode.Instant));
    protected final Setting<Boolean> inWater =
            register(new BooleanSetting("InWater", false));
    protected final Setting<Double> strafeSpeed =
            register(new NumberSetting<>("StrafeSpeed", 0.2873, 0.1, 1.0));
    protected final Setting<Double> speedSet =
            register(new NumberSetting<>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Integer> constTicks    =
            register(new NumberSetting<>("ConstTicks", 10, 1, 40));
    protected final Setting<Integer> constOff    =
            register(new NumberSetting<>("ConstOff", 3, 1, 10));
    protected final Setting<Double> constFactor    =
            register(new NumberSetting<>("ConstFactor", 2.149, 1.0, 5.0));
    protected final Setting<Boolean> useTimer    =
            register(new BooleanSetting("UseTimer", false));
    protected final Setting<Boolean> sneakCheck =
            register(new BooleanSetting("SneakCheck", false));
    protected final Setting<Boolean> explosions  =
            register(new BooleanSetting("Explosions", false));
    protected final Setting<Boolean> velocity  =
            register(new BooleanSetting("Velocity", false));
    protected final Setting<Float> multiplier    =
            register(new NumberSetting<>("H-Factor", 1.0f, 0.0f, 5.0f));
    protected final Setting<Float> vertical      =
            register(new NumberSetting<>("V-Factor", 1.0f, 0.0f, 5.0f));
    protected final Setting<Integer> coolDown    =
            register(new NumberSetting<>("CoolDown", 1000, 0, 5000));
    protected final Setting<Boolean> directional =
            register(new BooleanSetting("Directional", false));
    protected final Setting<Boolean> lagOut      =
            register(new BooleanSetting("LagOutBlocks", false));
    protected final Setting<Integer> lagTime     =
            register(new NumberSetting<>("LagTime", 500, 0, 1000));
    protected final Setting<Double> cap =
            register(new NumberSetting<>("Cap", 10.0, 0.0, 10.0));
    protected final Setting<Boolean> scaleCap      =
            register(new BooleanSetting("ScaleCap", false));
    protected final Setting<Boolean> slow =
            register(new BooleanSetting("Slowness", false));
    protected final Setting<Boolean> noWaterInstant =
            register(new BooleanSetting("NoLiquidInstant", false));
    protected final Setting<Boolean> noCollisionInstant =
            register(new BooleanSetting("NoCollisionInstant", false));
    protected final Setting<Boolean> modify =
            register(new BooleanSetting("Modify", false));
    protected final Setting<Double> xzFactor    =
            register(new NumberSetting<>("XZ-Factor", 1.0, 0.0, 5.0));
    protected final Setting<Double> yFactor    =
            register(new NumberSetting<>("Y-Factor", 1.0, 0.0, 5.0));

    protected final StopWatch expTimer = new StopWatch();

    protected boolean stop;
    protected int vanillaStage;
    protected int onGroundStage;
    protected int oldGroundStage;
    protected double speed;
    protected double distance;
    protected int gayStage;
    protected int stage;
    protected int ncpStage;
    protected int bhopStage;
    protected int vStage;
    protected int lowStage;
    protected int constStage;
    protected double lastExp;
    protected double lastDist;
    protected boolean boost;

    public Speed() {
        super("Speed", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerExplosion(this));
        this.listeners.add(new ListenerBlockPush(this));
        this.listeners.add(new ListenerVelocity(this));
        this.setData(new SpeedData(this));
    }

    @Override
    protected void onEnable()
    {
        if (mc.player != null)
        {
            speed = MovementUtil.getSpeed();
            distance = MovementUtil.getDistance2D();
        }

        vanillaStage   = 0;
        onGroundStage  = 2;
        oldGroundStage = 2;
        ncpStage    = 0;
        gayStage    = 1;
        vStage      = 1;
        bhopStage   = 4;
        stage       = 4;
        lowStage    = 4;
        lastDist = 0;
        constStage = 0;
    }

    @Override
    protected void onDisable()
    {
        Managers.TIMER.reset();
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().toString();
    }

    protected boolean notColliding()
    {
        boolean stepping = false;
        List<VoxelShape> collisions =
                CollectionUtil.asList(mc.world.getBlockCollisions(mc.player,
                        mc.player.getBoundingBox().expand(0.1, 0.0, 0.1)));
        if (STEP.isEnabled() && !collisions.isEmpty())
        {
            stepping = true;
        }

        return mc.player.isOnGround()
                && !stepping
                && !PositionUtil.inLiquid()
                && !PositionUtil.inLiquid(true);
    }

    public double getCap()
    {
        double ret = cap.getValue();

        if (!scaleCap.getValue())
        {
            return ret;
        }

        if (mc.player.hasStatusEffect(StatusEffects.SPEED))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SPEED))
                    .getAmplifier();

            ret *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (slow.getValue() && mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
        {
            int amplifier = Objects.requireNonNull(
                            mc.player.getStatusEffect(StatusEffects.SLOWNESS))
                    .getAmplifier();

            ret /= 1.0 + 0.2 * (amplifier + 1);
        }

        return ret;
    }

    public SpeedMode getMode()
    {
        return mode.getValue();
    }
}
