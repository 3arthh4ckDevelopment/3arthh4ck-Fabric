package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.movement.PositionManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends
        ModuleListener<BlockHighlight, MotionUpdateEvent>
{
    public ListenerMotion(BlockHighlight module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.POST
                && module.distance.getValue()
                && module.current != null
                && mc.player != null
                && mc.crosshairTarget != null
                && mc.crosshairTarget.getPos() != null)
        {
            HitResult r = mc.crosshairTarget;

            double d;
            boolean see;
            double x;
            double y;
            double z;
            boolean noPosition = false;
            //noinspection ConstantConditions
            if (r.getType() == HitResult.Type.BLOCK
                    && r.getPos() != null)
            {
                BlockPos p = BlockPos.ofFloored(r.getPos());
                if (module.hitVec.getValue())
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                                ? mc.player.getEyeHeight(mc.player.getPose())
                                                : 0.0,
                                              0.0)
                                         .distanceTo(r.getPos());
                }
                else if (module.toCenter.getValue())
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                             ? mc.player.getEyeHeight(mc.player.getPose())
                                             : 0.0, 0.0)
                                         .distanceTo(
                     new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5));
                }
                else
                {
                    d = Managers.POSITION.getVec()
                                         .add(0.0, module.eyes.getValue()
                                             ? mc.player.getEyeHeight(mc.player.getPose())
                                             : 0.0, 0.0)
                                         .distanceTo(new Vec3d(p.getX(), p.getY(), p.getZ()));
                }

                x = p.getX();
                y = p.getY();
                z = p.getZ();

                see = canSee(r.getPos(), Managers.POSITION);
            }
            else if (r.getType() == HitResult.Type.ENTITY
                    && mc.crosshairTarget instanceof EntityHitResult entityHitResult)
            {
                Entity e = entityHitResult.getEntity();
                d = Math.sqrt(e.squaredDistanceTo(Managers.POSITION.getX(),
                                            Managers.POSITION.getY()
                                                + (module.eyes.getValue()
                                                    ? mc.player.getEyeHeight(mc.player.getPose())
                                                    : 0.0),
                                            Managers.POSITION.getZ()));

                see = canSee(
                    new Vec3d(e.getX(), e.getY() + e.getEyeHeight(mc.player.getPose()), e.getZ()),
                    Managers.POSITION);

                x = e.getX();
                y = e.getY();
                z = e.getZ();
            }
            else
            {
                d = Managers.POSITION.getVec()
                                     .add(0.0,
                                          module.eyes.getValue()
                                            ? mc.player.getEyeHeight(mc.player.getPose())
                                            : 0.0,
                                          0.0)
                                     .distanceTo(r.getPos());

                see = canSee(r.getPos(), Managers.POSITION);
                x = y = z = 0.0;
                noPosition = true;
            }

            StringBuilder builder = new StringBuilder(module.current);
            builder.append(", ");

            if (d >= 6.0)
            {
                builder.append(TextColor.RED);
            }
            else if (d >= 3.0 && !see)
            {
                builder.append(TextColor.GOLD);
            }
            else
            {
                builder.append(TextColor.GREEN);
            }

            builder.append(MathUtil.round(d, 2));
            if (module.position.getValue() && !noPosition)
            {
                builder.append(TextColor.WHITE).append(", ").append(MathUtil.round(x, 2)).append(TextColor.GRAY).append("x")
                       .append(TextColor.WHITE).append(", ").append(MathUtil.round(y, 2)).append(TextColor.GRAY).append("y")
                       .append(TextColor.WHITE).append(", ").append(MathUtil.round(z, 2)).append(TextColor.GRAY).append("z");
            }

            module.current = builder.toString();
        }
    }

    private boolean canSee(Vec3d toSee, PositionManager m)
    {
        return RayTraceUtil.canBeSeen(toSee,
                                      m.getX(),
                                      m.getY(),
                                      m.getZ(),
                                      mc.player.getEyeHeight(mc.player.getPose()));
    }

}
