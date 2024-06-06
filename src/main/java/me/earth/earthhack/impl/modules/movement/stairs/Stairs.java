package me.earth.earthhack.impl.modules.movement.stairs;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;

public class Stairs extends Module {
    private final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 100, 0, 1000));
    private final Setting<Boolean> whileSneaking =
            register(new BooleanSetting("WhileSneaking", false));

    private final BlockPos.Mutable pos = new BlockPos.Mutable();
    private final StopWatch timer = new StopWatch();
    private double currentY;
    private double lastY;

    public Stairs() {
        super("Stairs", Category.Movement);
        SimpleData data = new SimpleData(this, "Makes you faster on stairs.");
        data.register(delay, "Delay in milliseconds between jumps.");
        this.setData(data);
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player.getY() != currentY || timer.passed(100)) {
                if (currentY != lastY) {
                    lastY = currentY;
                }

                currentY = mc.player.getY();
            }

            if (timer.passed(delay.getValue())
                    && mc.player.onGround
                    && mc.player.forwardSpeed > 0
                    && lastY < currentY
                    && !mc.player.isSpectator()
                    && !mc.player.isRiding()
                    && !mc.player.isHoldingOntoLadder()
                    && (!mc.player.isSneaking() || whileSneaking.getValue())
                    && checkForStairs()) {
                mc.player.jump();
                timer.reset();
            }
        }));
    }

    private boolean checkForStairs() {
        pos.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (mc.world.getBlockState(pos).getBlock() instanceof StairsBlock) {
            return true;
        }

        pos.set(mc.player.getX(), mc.player.getY() - 1, mc.player.getZ());
        return mc.world.getBlockState(pos).getBlock() instanceof StairsBlock;
    }

}