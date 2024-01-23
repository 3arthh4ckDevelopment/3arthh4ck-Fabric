package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.player.spectate.PlayerEntityNoInterp;
import me.earth.earthhack.impl.modules.render.nametags.IEntityNoNametag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.UUID;

public class MotionTracker extends PlayerEntityNoInterp
    implements Globals, IEntityNoNametag
{
    public double extraPosX;
    public double extraPosY;
    public double extraPosZ;

    public double lastExtraPosX;
    public double lastExtraPosY;
    public double lastExtraPosZ;

    public PlayerEntity tracked;
    public volatile boolean active;
    public boolean shrinkPush;
    public boolean gravity;
    public double gravityFactor = 1.0;
    public double yPlusFactor = 1.0;
    public double yMinusFactor = 1.0;
    public int ticks;

    public MotionTracker(ClientWorld worldIn, PlayerEntity from)
    {
        super(worldIn, new GameProfile(from.getGameProfile().getId(), "Motion-Tracker-" + from.getName()));
        this.tracked = from;
        this.setId(from.getId() * -1);
        this.copyPositionAndRotation(from);
    }

    @SuppressWarnings("unused")
    private MotionTracker(ClientWorld worldIn) // to appease the minecraft development intellij plugin do not use >:(
    {
        super(worldIn, new GameProfile(UUID.randomUUID(), "Motion-Tracker"));
    }

    public void resetMotion()
    {
        this.setVelocity(0, 0, 0);
    }

    // TODO: this is kinda bad
    public void pushOutOfBlocks(PushMode mode)
    {
        Box Box = shrinkPush
            ? this.getBoundingBox().expand(-0.0625, -0.0625, -0.0625)
            : this.getBoundingBox();

        // TODO: smarter way than calling this 4 times?????
        mode.pushOutOfBlocks(this, this.getX() - (double)this.getWidth() * 0.35D, Box.minY + 0.5D, this.getZ() + (double)this.getWidth() * 0.35D);
        mode.pushOutOfBlocks(this, this.getX() - (double)this.getWidth() * 0.35D, Box.minY + 0.5D, this.getZ() - (double)this.getWidth() * 0.35D);
        mode.pushOutOfBlocks(this, this.getX() + (double)this.getWidth() * 0.35D, Box.minY + 0.5D, this.getZ() - (double)this.getWidth() * 0.35D);
        mode.pushOutOfBlocks(this, this.getX() + (double)this.getWidth() * 0.35D, Box.minY + 0.5D, this.getZ() + (double)this.getWidth() * 0.35D);
    }

    public void updateFromTrackedEntity()
    {
        this.setVelocity(tracked.getVelocity().getX(),
                    tracked.getVelocity().getY() > 0.0 ? tracked.getVelocity().getY() * yPlusFactor : tracked.getVelocity().getY() * yMinusFactor,
                    tracked.getVelocity().getZ());

        if (gravity) {
            this.setVelocity(tracked.getVelocity().getX(), tracked.getVelocity().getY() - 0.03999999910593033D * gravityFactor * ticks, tracked.getVelocity().getZ()); // * 0.9800000190734863D ?
        }
        /*
        List<VoxelShape> list1 = this.getWorld().getEntityCollisions(this, this.getBoundingBox().expand(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ()));
        if (this.getVelocity().getY() != 0.0D)
        {
            int k = 0;

            for (int l = list1.size(); k < l; ++k)
            {
                this.motionY = list1.get(k).getBoundingBox().calculateYOffset(this.getBoundingBox(), this.getVelocity().getY());
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, this.getVelocity().getY(), 0.0D));
        }

        if (this.getVelocity().getX() != 0.0D)
        {
            int j5 = 0;

            for (int l5 = list1.size(); j5 < l5; ++j5)
            {
                this.motionX = list1.get(j5).getBoundingBox().calculateXOffset(this.getBoundingBox(), this.getVelocity().getX());
            }

            if (this.getVelocity().getX() != 0.0D)
            {
                this.setBoundingBox(this.getBoundingBox().offset(this.getVelocity().getX(), 0.0D, 0.0D));
            }
        }

        if (this.getVelocity().getY() != 0.0D)
        {
            int k5 = 0;

            for (int i6 = list1.size(); k5 < i6; ++k5)
            {
                this.motionZ = list1.get(k5).calculateZOffset(this.getBoundingBox(), this.getVelocity().getZ());
            }

            if (this.getVelocity().getZ() != 0.0D)
            {
                this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, this.getVelocity().getZ()));
            }
        }
        //TODO: solve
         */

        this.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
        this.setOnGround(tracked.isOnGround());
        this.prevX = tracked.prevX;
        this.prevY = tracked.prevY;
        this.prevZ = tracked.prevZ;
        this.collidedSoftly = tracked.collidedSoftly;
        this.horizontalCollision = tracked.horizontalCollision;
        this.verticalCollision = tracked.verticalCollision;
        this.forwardSpeed = tracked.forwardSpeed;
        this.sidewaysSpeed = tracked.sidewaysSpeed;
        this.horizontalSpeed = tracked.horizontalSpeed;
        this.speed = tracked.speed;
        this.lastRenderX = getX();
        this.lastRenderY = getY();
        this.lastRenderZ = getZ();
        this.lastExtraPosX = extraPosX;
        this.lastExtraPosY = extraPosY;
        this.lastExtraPosZ = extraPosZ;
    }

    @Override
    public boolean isSpectator()
    {
        return false;
    }

    @Override
    public boolean isCreative()
    {
        return false;
    }

}
