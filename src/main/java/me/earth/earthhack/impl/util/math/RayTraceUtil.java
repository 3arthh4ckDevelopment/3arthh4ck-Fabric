package me.earth.earthhack.impl.util.math;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;

// TODO: better rayTrace for 2b2t. Find the part of the block we can see
public class RayTraceUtil implements Globals
{
    private static final SettingCache
            <Boolean, BooleanSetting, Management> NEW_PLACE =
            Caches.getSetting(Management.class, BooleanSetting.class, "1.19-Place", false);
    /**
     * Produces a float array of length 3 representing
     * the needed facingX, facingY and facingZ for a
     * CPacketPlayerTryUseItemOnBlock. A similar calculation
     * is made in {@link net.minecraft.client.network.ClientPlayerInteractionManager#interactBlock(ClientPlayerEntity, Hand, BlockHitResult)}.
     *
     * @param pos the pos.
     * @param hitVec the hitVec.
     * @return facingX, facingY and facingZ.
     */
    public static float[] hitVecToPlaceVec(BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        // TODO: also fix any outgoing packet?
        if (NEW_PLACE.getValue()) {
            if (!(Math.abs(x - 0.5) < 1.0000001f && Math.abs(y - 0.5) < 1.0000001 && Math.abs(z - 0.5) < 1.0000001)) {
                return new float[]{(float) hitVec.x,(float)  hitVec.y, (float) hitVec.z};
            }
        }

        return new float[]{(float) x, (float) y, (float) z};
    }

    public static BlockHitResult getBlockHitResult(float yaw, float pitch)
    {
        return getBlockHitResult(yaw, pitch, (float) mc.player.getAttributes().getValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE));
    }

    public static BlockHitResult getBlockHitResultWithEntity(float yaw, float pitch, Entity from)
    {
        return getBlockHitResult(yaw, pitch, (float) mc.player.getAttributes().getValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE), from);
    }
    
    public static BlockHitResult getBlockHitResult(float yaw, float pitch, float distance)
    {
        return getBlockHitResult(yaw, pitch, distance, mc.player);
    }

    public static BlockHitResult getBlockHitResult(float yaw, float pitch, float d, Entity from)
    {
        Vec3d vec3d     = PositionUtil.getEyePos(from);
        Vec3d lookVec   = RotationUtil.getVec3d(yaw, pitch);
        Vec3d rotations = vec3d.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);

        return Optional.ofNullable(
            mc.world.raycast(new RaycastContext(vec3d, rotations, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, from)))
                      .orElseGet(() ->
          new BlockHitResult(new Vec3d(0.5, 1.0, 0.5), Direction.UP, BlockPos.ORIGIN, false));
    }

    public static boolean canBeSeen(double x, double y, double z, Entity by)
    {
        return canBeSeen(new Vec3d(x, y, z), by.getX(), by.getY(), by.getZ(), by.getEyeHeight(by.getPose()));
    }

    public static boolean canBeSeen(Vec3d toSee, Entity by)
    {
        return canBeSeen(toSee, by.getX(), by.getY(), by.getZ(), by.getEyeHeight(by.getPose()));
    }

    public static boolean canBeSeen(Vec3d toSee, double x, double y, double z, float eyeHeight)
    {
        Vec3d start = new Vec3d(x, y + eyeHeight, z);
        return mc.world.raycast(new RaycastContext(start, toSee, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).isInsideBlock(); //TODO: check
    }

    public static boolean canBeSeen(Entity toSee, LivingEntity by)
    {
        return by.canSee(toSee);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean raytracePlaceCheck(Entity entity, BlockPos pos)
    {
        return getFacing(entity, pos, false) != null;
    }

    public static Direction getFacing(Entity entity,
                                       BlockPos pos,
                                       boolean verticals)
    {
        for (Direction facing : Direction.values())
        {
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    PositionUtil.getEyePos(entity),
                    new Vec3d(
                            pos.getX() + 0.5 + facing.getVector().getX() * 1.0 / 2.0,
                            pos.getY() + 0.5 + facing.getVector().getY() * 1.0 / 2.0,
                            pos.getZ() + 0.5 + facing.getVector().getZ() * 1.0 / 2.0),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    entity
            ));

            if (result != null
                    && result.getType() == BlockHitResult.Type.BLOCK
                    && result.getBlockPos().equals(pos))
            {
                return facing;
            }
        }

        if (verticals)
        {
            if (pos.getY() > mc.player.getY() + mc.player.getEyeHeight(entity.getPose()))
            {
                return Direction.DOWN;
            }

            return Direction.UP;
        }

        return null;
    }

}
