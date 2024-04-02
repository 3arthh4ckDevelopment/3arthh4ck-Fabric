package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class LongJump extends Module
{
    protected final Setting<JumpMode> mode     =
            register(new EnumSetting<>("Mode", JumpMode.Normal));
    protected final Setting<Double> boost      =
            register(new NumberSetting<>("Boost", 4.5, 0.1, 20.0));
    protected final Setting<Boolean> noKick    =
            register(new BooleanSetting("AntiKick", true));
    protected final Setting<Boolean> pauseSpeed    =
            register(new BooleanSetting("PauseSpeed", false));
    protected final Setting<Boolean> speedCheck    =
            register(new BooleanSetting("SpeedCheck", false));
    protected final Setting<Integer> speedTimeout      =
            register(new NumberSetting<>("Timeout", 100, 1, 500));
    protected final Setting<Bind> invalidBind    =
            register(new BindSetting("Invalid", Bind.fromKey(Keyboard.getKeyM())));

    private static final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);

    protected int stage;
    protected int airTicks;
    protected int groundTicks;
    protected double speed;
    protected double distance;
    protected boolean wasSpeedActive;
    StopWatch timeoutTimer = new StopWatch();

    public LongJump() {
        super("Longjump", Category.Movement);
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerPosLook(this));
        this.setData(new LongJumpData(this));
    }

    @Override
    protected void onEnable()
    {
        wasSpeedActive = speedCheck.getValue() && SPEED.isEnabled();

        if(SPEED.isEnabled() && pauseSpeed.getValue())
        {
            timeoutTimer.reset();
            SPEED.disable();
        }

        if (mc.player != null)
        {
            distance = MovementUtil.getDistance2D();
            speed    = MovementUtil.getSpeed();
        }

        stage       = 0;
        airTicks    = 0;
        groundTicks = 0;
    }

    @Override
    protected void onDisable()
    {
        Managers.TIMER.reset();
        if (!SPEED.isEnabled() && pauseSpeed.getValue() && timeoutTimer.passed(speedTimeout.getValue())) {
            if (speedCheck.getValue()) {
                if (wasSpeedActive)
                    SPEED.enable();
            }
            else
                 SPEED.enable();
        }
    }

    protected void invalidPacket()
    {
        updatePosition(0.0, Integer.MAX_VALUE, 0.0);
    }

    protected void updatePosition(double x, double y, double z)
    {
        mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, mc.player.isOnGround()));
    }

    protected double getDistance(PlayerEntity player, double distance)
    {
        List<Box> boundingBoxes = new ArrayList<>();
        // hella hacky again, can probably be better
        mc.world.getCollisions(
                player,
                player.getBoundingBox().offset(0, -distance, 0))
                    .forEach(box -> boundingBoxes.add(box.getBoundingBox()));


        if (boundingBoxes.isEmpty())
        {
            return 0.0;
        }

        double y = 0.0;
        for (Box boundingBox : boundingBoxes)
        {
            if (boundingBox.maxY > y)
            {
                y = boundingBox.maxY;
            }
        }
        return player.getY() - y;
    }
}
