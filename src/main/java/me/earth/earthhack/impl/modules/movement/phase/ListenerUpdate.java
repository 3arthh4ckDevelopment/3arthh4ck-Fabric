package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;

final class ListenerUpdate extends ModuleListener<Phase, UpdateEvent>
{
    public ListenerUpdate(Phase module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.mode.getValue() == PhaseMode.NoClip)
        {
            mc.player.noClip       = true;
            mc.player.onGround     = false;
            mc.player.fallDistance = 0;
        }

        if (module.mode.getValue() == PhaseMode.Constantiam
                && MovementUtil.isMoving()
                && module.constTeleport.getValue()
                && module.isPhasing()) {
            double multiplier = module.constSpeed.getValue();
            double mx = -Math.sin(Math.toRadians(this.mc.player.yaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.yaw));
            double x = (double) mc.player.input.movementForward * multiplier * mx + (double) mc.player.input.movementSideways * multiplier * mz;
            double z = (double) mc.player.input.movementForward * multiplier * mz - (double) mc.player.input.movementSideways * multiplier * mx;
            this.mc.player.setPosition(this.mc.player.getX() + x, this.mc.player.getY(), this.mc.player.getZ() + z);
        }

        if (module.mode.getValue() == PhaseMode.ConstantiamNew) {
            double multiplier = 0.3;
            double mx = -Math.sin(Math.toRadians(this.mc.player.yaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.yaw));
            double x = (double)mc.player.input.movementForward * multiplier * mx + (double)mc.player.input.movementSideways * multiplier * mz;
            double z = (double)mc.player.input.movementForward * multiplier * mz - (double)mc.player.input.movementSideways * multiplier * mx;
            if (mc.player.horizontalCollision && !this.mc.player.isHoldingOntoLadder()) {
                PacketUtil.doPosition(mc.player.getX() + x, mc.player.getY(), mc.player.getZ() + z, false);
                for (int i = 1; i < 10; ++i) {
                    PacketUtil.doPosition(mc.player.getX(),8.988465674311579E307, mc.player.getZ(), false);
                }
                this.mc.player.setPosition(this.mc.player.getX() + x, this.mc.player.getY(), this.mc.player.getZ() + z);
            }
        }
    }

}