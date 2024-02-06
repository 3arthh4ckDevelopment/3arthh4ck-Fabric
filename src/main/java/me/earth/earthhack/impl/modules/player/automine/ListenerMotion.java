package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.automine.mode.AutoMineMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.item.Items;

public class ListenerMotion extends ModuleListener<AutoMine, MotionUpdateEvent> {
    public ListenerMotion(AutoMine module){
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (!module.rotate.getValue()
                /*|| PingBypass.isConnected() && !event.isPingBypass()*/)
            return;

        if(event.getStage() == Stage.PRE
            && module.current != null
            && !PlayerUtil.isCreative(mc.player)
            && !Managers.ROTATION.isBlocking()
            && (!InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE)
                || mc.options.useKey.isPressed()))
        {
            module.rotations = RotationUtil
                    .getRotations(module.current, module.facing);
            /*
            * No need for a Speedmine implementation because it's
            * rotations are already possible in its own Settings.
            */
            if(module.mode.getValue() == AutoMineMode.Compatibility
                    && module.rotationTimer.passed(module.rotateLimit.getValue()))
            {
                set(event, module.rotations);
            }
        }
    }

    private void set(MotionUpdateEvent event, float[] rotations) {
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }
}
