package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraftClient;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Interpolation implements Globals
{
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);

    public static Vec3d interpolatedEyePos()
    {
        return mc.player.getCameraPosVec(mc.getTickDelta());
    }

    public static Vec3d interpolatedEyeVec()
    {
        return mc.player.getClientCameraPosVec(mc.getTickDelta());
    }

    public static Vec3d interpolatedEyeVec(PlayerEntity player) {
        return player.getClientCameraPosVec(mc.getTickDelta());
    }

    public static Vec3d interpolateEntity(Entity entity)
    {
        double x;
        double y;
        double z;

        if (NOINTERP.isEnabled()
                && NOINTERP.get().isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            x = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpX(), entity.lastRenderX)
                    - getRenderPosX();
            y = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpY(), entity.lastRenderY)
                    - getRenderPosY();
            z = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpZ(), entity.lastRenderZ)
                    - getRenderPosZ();
        }
        else
        {
            x = interpolateLastTickPos(entity.getPos().x, entity.lastRenderX)
                    - getRenderPosX();
            y = interpolateLastTickPos(entity.getPos().y, entity.lastRenderY)
                    - getRenderPosY();
            z = interpolateLastTickPos(entity.getPos().z, entity.lastRenderZ)
                    - getRenderPosZ();
        }

        return new Vec3d(x, y, z);
    }

    public static Vec3d interpolateEntityNoRenderPos(Entity entity)
    {
        double x;
        double y;
        double z;

        if (NOINTERP.isEnabled()
                && NOINTERP.get().isSilent()
                && entity instanceof IEntityNoInterp
                && ((IEntityNoInterp) entity).earthhack$isNoInterping())
        {
            x = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpX(), entity.lastRenderX);
            y = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpY(), entity.lastRenderY);
            z = interpolateLastTickPos(((IEntityNoInterp) entity).earthhack$getNoInterpZ(), entity.lastRenderZ);
        }
        else
        {
            x = interpolateLastTickPos(entity.getPos().x, entity.lastRenderX);
            y = interpolateLastTickPos(entity.getPos().y, entity.lastRenderY);
            z = interpolateLastTickPos(entity.getPos().z, entity.lastRenderZ);
        }

        return new Vec3d(x, y, z);
    }

    public static Vec3d interpolateVectors(Vec3d current, Vec3d last) {
        double x = interpolateLastTickPos(current.x, last.x) - getRenderPosX();
        double y = interpolateLastTickPos(current.y, last.y) - getRenderPosY();
        double z = interpolateLastTickPos(current.z, last.z) - getRenderPosZ();
        return new Vec3d(x, y, z);
    }

    public static double interpolateLastTickPos(double pos, double lastPos)
    {
        return lastPos + (pos - lastPos) * ((IMinecraftClient) mc).getTimer().tickDelta;
    }

    public static Box interpolatePos(BlockPos pos, float height)
    {
        return new Box(
                pos.getX() - mc.gameRenderer.getCamera().getPos().x,
                pos.getY() - mc.gameRenderer.getCamera().getPos().y,
                pos.getZ() - mc.gameRenderer.getCamera().getPos().z,
                pos.getX() - mc.gameRenderer.getCamera().getPos().x + 1,
                pos.getY() - mc.gameRenderer.getCamera().getPos().y + height,
                pos.getZ() - mc.gameRenderer.getCamera().getPos().z + 1);
    }

    public static Box interpolateAxis(Box bb)
    {
        return new Box(
                bb.minX - mc.gameRenderer.getCamera().getPos().x,
                bb.minY - mc.gameRenderer.getCamera().getPos().y,
                bb.minZ - mc.gameRenderer.getCamera().getPos().z,
                bb.maxX - mc.gameRenderer.getCamera().getPos().x,
                bb.maxY - mc.gameRenderer.getCamera().getPos().y,
                bb.maxZ - mc.gameRenderer.getCamera().getPos().z);
    }

    public static void interpolateMutable(MutableBB bb)
    {
        bb.minX = (int) (bb.minX - mc.gameRenderer.getCamera().getPos().x);
        bb.minY = (int) (bb.minY - mc.gameRenderer.getCamera().getPos().y);
        bb.minZ = (int) (bb.minZ - mc.gameRenderer.getCamera().getPos().z);
        bb.maxX = (int) (bb.maxX - mc.gameRenderer.getCamera().getPos().x);
        bb.maxY = (int) (bb.maxY - mc.gameRenderer.getCamera().getPos().y);
        bb.maxZ = (int) (bb.maxZ - mc.gameRenderer.getCamera().getPos().z);
    }

    public static Box offsetRenderPos(Box bb)
    {
        return bb.offset(-getRenderPosX(), -getRenderPosY(), -getRenderPosZ());
    }

    public static double getRenderPosX()
    {
        return mc.gameRenderer.getCamera().getPos().x;
    }

    public static double getRenderPosY()
    {
        return mc.gameRenderer.getCamera().getPos().y;
    }

    public static double getRenderPosZ()
    {
        return mc.gameRenderer.getCamera().getPos().z;
    }

    public static Frustum createFrustum(Entity entity)
    {
        Frustum frustum = new Frustum(
                mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue()), // chinese!!!
                mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue()));
        // NoInterp shouldn't really be required here
        setFrustum(frustum, entity);
        return frustum;
    }

    public static void setFrustum(Frustum frustum, Entity entity)
    {
        double x = interpolateLastTickPos(entity.getPos().x, entity.lastRenderX);
        double y = interpolateLastTickPos(entity.getPos().y, entity.lastRenderY);
        double z = interpolateLastTickPos(entity.getPos().z, entity.lastRenderZ);

        frustum.setPosition(x, y, z);

    }

}
