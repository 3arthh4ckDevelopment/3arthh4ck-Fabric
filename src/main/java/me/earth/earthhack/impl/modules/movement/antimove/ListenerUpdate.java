package me.earth.earthhack.impl.modules.movement.antimove;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.antimove.modes.StaticMode;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

final class ListenerUpdate extends ModuleListener<NoMove, UpdateEvent>
{
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);

    public ListenerUpdate(NoMove module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.mode.getValue() == StaticMode.NoVoid)
        {
            if (!mc.player.noClip
                    && mc.player.getY() <= module.height.getValue()
                    && !PACKET_FLY.isEnabled()
                    && mc.player.getAbilities().flying)
            {
                final BlockHitResult trace = mc.world.raycast(
                        new RaycastContext(mc.player.getPos(),
                                new Vec3d(mc.player.getX(), 0, mc.player.getZ()),
                                RaycastContext.ShapeType.COLLIDER,
                                RaycastContext.FluidHandling.NONE,
                                ShapeContext.absent()));

                if (trace == null
                        || trace.getType() != HitResult.Type.BLOCK)
                {
                    if (module.timer.getValue())
                    {
                        Managers.TIMER.setTimer(0.1f);
                    }
                    else
                    {
                        mc.player.setVelocity(0, 0, 0);
                        if (mc.player.getVehicle() != null)
                        {
                            mc.player.getVehicle().setVelocity(0, 0, 0);
                        }
                    }
                }
            }
        }
    }

}
