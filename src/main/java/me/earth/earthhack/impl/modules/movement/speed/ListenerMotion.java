package me.earth.earthhack.impl.modules.movement.speed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.player.freecam.Freecam;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.util.math.Direction;

final class ListenerMotion extends ModuleListener<Speed, MotionUpdateEvent>
{
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<Freecam> FREECAM =
            Caches.getModule(Freecam.class);

    public ListenerMotion(Speed module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (PACKET_FLY.isEnabled() || FREECAM.isEnabled())
        {
            return;
        }
        if(mc.player == null) return;


        if (MovementUtil.noMovementKeys())
        {
            mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.X, 0.0));
            mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Z, 0.0));
        }

        if (module.mode.getValue() == SpeedMode.OldGround) {

            switch (event.getStage()) {
                case PRE: {
                    if (module.notColliding()) {
                        module.oldGroundStage++;
                    } else {
                        module.oldGroundStage = 0;
                    }

                    if (module.oldGroundStage != 4)
                        break;

                    event.setY(event.getY()
                            + (PositionUtil.isBoxColliding()
                            ? 0.2
                            : 0.4)
                            + MovementUtil.getJumpSpeed());
                    break;
                }
                case POST: {
                    if (module.oldGroundStage == 3) {
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.X, mc.player.getVelocity().x * 3.25));
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Z, mc.player.getVelocity().z * 3.25));
                    } else if (module.oldGroundStage == 4) {
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.X, mc.player.getVelocity().x / 1.4));
                        mc.player.setVelocity(mc.player.getVelocity().withAxis(Direction.Axis.Z, mc.player.getVelocity().z / 1.4));
                        module.oldGroundStage = 2;
                    }
                }
            }
        }

        module.distance = MovementUtil.getDistance2D();
        if (module.mode.getValue() == SpeedMode.OnGround)
        {
            if (module.onGroundStage == 3)
            {
                event.setY(event.getY()
                        + (PositionUtil.isBoxColliding()
                        ? 0.2
                        : 0.4)
                        + MovementUtil.getJumpSpeed());
            }
        }
    }

}
