package me.earth.earthhack.impl.modules.player.phobotspeedmine;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Collections;
import java.util.List;

public class PhobotSpeedmine extends Module
{
    private final Setting<Boolean> fast =
            register(new BooleanSetting("Fast", true));
    private final Setting<Boolean> silentSwitch =
            register(new BooleanSetting("Switch", true));
    private final Setting<Boolean> noGlitchBlocks =
            register(new BooleanSetting("NoGlitchBlocks", false));
    private final Setting<Boolean> swing =
            register(new BooleanSetting("Swing", false));
    private final Setting<Boolean> addTick =
            register(new BooleanSetting("AddTick", false))
                    .setComplexity(Complexity.Dev);

    private final StopWatch expectingAirTimer = new StopWatch();
    private final StopWatch timer = new StopWatch();

    private BlockPos currentPos = null;
    private BlockState currentState = Blocks.AIR.getDefaultState();
    private List<Box> renderBBs = Collections.emptyList();
    private float renderDamageDelta = 0.0f;
    private int renderTicks = 0;
    private boolean sendAbortNextTick = true;
    private boolean expectingAir;

    public PhobotSpeedmine() {
        super("PhobotSpeedmine", Category.Player);
        this.listeners.add(new ListenerDamageBlock(this));
        timer.reset();
    }

    protected void reset() {
        currentPos = null;
        currentState = Blocks.AIR.getDefaultState();
        renderBBs = Collections.emptyList();
        renderDamageDelta = 0.0f;
        renderTicks = 0;
        sendAbortNextTick = true;
        timer.reset();
        expectingAir = false;
    }

}
