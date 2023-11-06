package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Manages the last position that has been
 * reported to or, via SPacketPlayerPosLook,
 * by the server.
 */
public class PositionManager extends SubscriberImpl implements Globals
{
    private static final boolean SET_SELF = Boolean.parseBoolean(
            System.getProperty("set.mc.player.serverPos", "true"));

    private boolean blocking;

    private volatile int teleportID;
    private volatile double last_x;
    private volatile double last_y;
    private volatile double last_z;
    private volatile boolean onGround;

    public PositionManager()
    {
        // TODO Just changed all prios to MIN_VALUE, check if that is fine
        this.listeners.add(
                new EventListener<PacketEvent.Receive<EntityPositionS2CPacket>>
                        (PacketEvent.Receive.class,
                                Integer.MIN_VALUE,
                                EntityPositionS2CPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Receive<EntityPositionS2CPacket> event)
                    {
                        PlayerEntity player = mc.player;
                        if (player == null) {
                            if (!mc.isOnThread()) {
                                mc.execute(() -> this.invoke(event));
                            }

                            return;
                        }

                        EntityPositionS2CPacket packet = event.getPacket();
                        double x = packet.getX();
                        double y = packet.getY();
                        double z = packet.getZ();

                        if (packet.getFlags()
                                .contains(EntityPositionS2CPacket.EnumFlags.X))
                        {
                            x += player.posX;
                        }

                        if (packet.getFlags()
                                .contains(EntityPositionS2CPacket .EnumFlags.Y))
                        {
                            y += player.posY;
                        }

                        if (packet.getFlags()
                                .contains(EntityPositionS2CPacket.EnumFlags.Z))
                        {
                            z += player.posZ;
                        }

                        last_x = MathHelper.clamp(x, -3.0E7, 3.0E7);
                        last_y = y;
                        last_z = MathHelper.clamp(z, -3.0E7, 3.0E7);
                        if (SET_SELF) {
                            player.serverPosX = EntityTracker.getPositionLong(last_x);
                            player.serverPosY = EntityTracker.getPositionLong(last_y);
                            player.serverPosZ = EntityTracker.getPositionLong(last_z);
                        }

                        onGround = false;
                        teleportID = packet.getId();
                    }
                });
        this.listeners.add(
                new EventListener<PacketEvent.Post<PlayerMoveC2SPacket.PositionAndOnGround>>
                        (PacketEvent.Post.class,
                                Integer.MIN_VALUE,
                                PlayerMoveC2SPacket.PositionAndOnGround.class)
                {
                    @Override
                    public void invoke(PacketEvent.Post<PlayerMoveC2SPacket.PositionAndOnGround> event)
                    {
                        readCPacket(event.getPacket());
                    }
                });
        this.listeners.add(
                new EventListener
                        <PacketEvent.Post<PlayerMoveC2SPacket.Full>>
                        (PacketEvent.Post.class,
                                Integer.MIN_VALUE,
                                PlayerMoveC2SPacket.Full.class)
                {
                    @Override
                    public void invoke
                            (PacketEvent.Post<PlayerMoveC2SPacket.Full> event)
                    {
                        readCPacket(event.getPacket());
                    }
                });
    }

    public int getTeleportID()
    {
        return teleportID;
    }

    public double getX()
    {
        return last_x;
    }

    public double getY()
    {
        return last_y;
    }

    public double getZ()
    {
        return last_z;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public Box getBB()
    {
        double x = this.last_x;
        double y = this.last_y;
        double z = this.last_z;
        float w = mc.player.getWidth() / 2.0f;
        float h = mc.player.getHeight();
        return new Box(x - w, y, z - w, x + w, y + h, z + w);
    }

    public Vec3d getVec()
    {
        return new Vec3d(last_x, last_y, last_z);
    }

    public void readCPacket(PlayerMoveC2SPacket packetIn)
    {
        last_x = packetIn.getX(mc.player.getX());
        last_y = packetIn.getY(mc.player.getY());
        last_z = packetIn.getZ(mc.player.getZ());
        PlayerEntity player;
        if (SET_SELF && (player = mc.player) != null) {
            player.serverPosX = EntityTracker.getPositionLong(last_x);
            player.serverPosY = EntityTracker.getPositionLong(last_y);
            player.serverPosZ = EntityTracker.getPositionLong(last_z);
        }

        setOnGround(packetIn.isOnGround());
    }

    public double getDistanceSq(Entity entity)
    {
        return getDistanceSq(entity.getX(), entity.getY(), entity.getZ());
    }

    public double getDistanceSq(double x, double y, double z)
    {
        double xDiff = last_x - x;
        double yDiff = last_y - y;
        double zDiff = last_z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEntityBeSeen(Entity entity)
    {
        // return mc.world.raycastBlock(
        //         new Vec3d(last_x, last_y + mc.player.getEyeHeight(entity.getPose()), last_z),
        //         new Vec3d(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ()),
        //         false,
        //         true,
        //         false) == null;
    }

    public void set(double x, double y, double z)
    {
        this.last_x = x;
        this.last_y = y;
        this.last_z = z;
    }

    public void setOnGround(boolean onGround)
    {
        this.onGround = onGround;
    }

    /**
     * Makes {@link PositionManager#isBlocking()} return the given
     * argument, that won't prevent other modules from
     * spoofing positions, but they can check it. For more info
     * see {@link RotationManager#setBlocking(boolean)}.
     *
     * Remember to set this to false after
     * the Rotations have been sent.
     *
     * @param blocking blocks position spoofing
     */
    public void setBlocking(boolean blocking)
    {
        this.blocking = blocking;
    }

    /**
     * Indicates that a module is currently
     * spoofing the position and it shouldn't
     * be spoofed by others.
     *
     * @return <tt>true</tt> if blocking.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

}
