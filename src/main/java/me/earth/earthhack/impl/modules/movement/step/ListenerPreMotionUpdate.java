package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;

final class ListenerPreMotionUpdate
    extends ModuleListener<Step, MotionUpdateEvent> {
    public ListenerPreMotionUpdate(Step module) {
        super(module, MotionUpdateEvent.class, 15_000);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        Box bb = module.bb;
        double[] offsets = module.offsets;

        if (module.stepping
            && module.mode.getValue() == StepMode.Slow
            && event.getStage() == Stage.PRE
            && offsets != null
            && bb != null) {
            boolean noMovementKeys = MovementUtil.noMovementKeys();
            if (module.index++ < offsets.length && !noMovementKeys) {
                if (module.useTimer.getValue() && module.index == offsets.length - 1) {
                    Managers.TIMER.reset();
                }
                double y = (module.index / (double) offsets.length)
                    * module.currHeight;

                mc.player.setPosition(module.x, module.y + y, module.z);
                event.setCancelled(true);
            } else if (noMovementKeys) {
                module.reset();
            } else {
                for (double offset : offsets) {
                    mc.player.networkHandler.sendPacket(
                            new PlayerMoveC2SPacket.PositionAndOnGround(
                                    module.x,
                                    module.y + offset,
                                    module.z,
                                    true));

                /*
                if (PingBypassModule.CACHE.isEnabled()
                    && !PingBypassModule.CACHE.get().isOld()) {
                    mc.player.networkHandler.sendPacket(
                        new C2SStepPacket(
                            offsets, module.x, module.y, module.z));
                 */
                }

                mc.player.setBoundingBox(bb);
                mc.player.resetPosition();
                module.reset();
                if (module.autoOff.getValue()) {
                    module.disable();
                }
            }
        } else if (module.stepping && (bb == null || offsets == null)) {
            module.reset();
        }
    }

}
