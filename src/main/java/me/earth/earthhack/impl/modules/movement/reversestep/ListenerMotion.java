package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

final class ListenerMotion extends ModuleListener<ReverseStep, MotionUpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG =
            Caches.getModule(BlockLag.class);
    private static final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);
    private static final ModuleCache<LongJump> LONGJUMP =
            Caches.getModule(LongJump.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed>
            SPEED_MODE = Caches.getSetting(
                    Speed.class, Setting.class, "Mode", SpeedMode.Instant);

    private boolean reset = false;
    private final StopWatch speedTimer = new StopWatch();
    public ListenerMotion(ReverseStep module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {

        if (event.getStage() == Stage.POST) {
            if (PositionUtil.inLiquid(true)
                    || PositionUtil.inLiquid(false)
                    || PACKET_FLY.isEnabled()
                    || BLOCK_LAG.isEnabled()
                    || LONGJUMP.isEnabled()) {
                reset = true;
                return;
            }

            if(SPEED.isEnabled()
              && SPEED_MODE.getValue() != SpeedMode.Instant)
            {
                speedTimer.reset();
            }

            if(!speedTimer.passed(300)) return;

            final List<EnderPearlEntity> pearls = CollectionUtil.asList(mc.world.getEntities())
                    .stream()
                    .filter(EnderPearlEntity.class::isInstance)
                    .map(EnderPearlEntity.class::cast)
                    .toList();
            if (!pearls.isEmpty()) {
                module.waitForOnGround = true;
            }
            if (!mc.player.onGround) {
                if (mc.options.jumpKey.isPressed()) {
                    module.jumped = true;
                }
            } else {
                module.jumped = false;
                reset = false;
                module.waitForOnGround = false;
            }

            if (!module.jumped
                    && mc.player.fallDistance < 0.5
                    && mc.player.getY() - module.getNearestBlockBelow() > 0.625
                    && mc.player.getY() - module.getNearestBlockBelow() <= module.distance.getValue()
                    && !reset
                    && !module.waitForOnGround) {
                if (!mc.player.onGround) {
                    module.packets++;
                }

                if (!mc.player.onGround && mc.player.getVelocity().y < 0
                        && !(mc.player.isHoldingOntoLadder()
                        // || mc.player.isEntityInsideOpaqueBlock())
                        && (!module.strictLiquid.getValue() || (!mc.player.isInLava() && !mc.player.isInsideWaterOrBubbleColumn()))
                        && !mc.options.jumpKey.isPressed()
                        && module.packets > 0)) {

                    mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Y, mc.player.getVelocity().y - module.speed.getValue()));
                    module.packets = 0;
                }
            }
        }
    }

    private boolean isLiquid(BlockPos position) {
        Fluid fluid = mc.world.getFluidState(position).getFluid();
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA || fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }

}
