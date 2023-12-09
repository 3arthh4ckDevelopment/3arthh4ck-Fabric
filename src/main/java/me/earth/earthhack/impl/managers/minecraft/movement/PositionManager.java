package me.earth.earthhack.impl.managers.minecraft.movement;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
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
                new EventListener<PacketEvent.Receive<PlayerPositionLookS2CPacket>>
                        (PacketEvent.Receive.class,
                                Integer.MIN_VALUE,
                                EntityPositionS2CPacket.class)
                {
                    @Override
                    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
                    {
                        PlayerEntity player = mc.player;
                        if (player == null) {
                            if (!mc.isOnThread()) {
                                mc.execute(() -> this.invoke(event));
                            }

                            return;
                        }

                        PlayerPositionLookS2CPacket packet = event.getPacket();
                        double x = packet.getX();
                        double y = packet.getY();
                        double z = packet.getZ();

                        if (packet.getFlags()
                                .contains(PositionFlag.X))
                        {
                            x += player.getX();
                        }

                        if (packet.getFlags()
                                .contains(PositionFlag.Y))
                        {
                            y += player.getY();
                        }

                        if (packet.getFlags()
                                .contains(PositionFlag.Z))
                        {
                            z += player.getZ();
                        }

                        last_x = MathHelper.clamp(x, -3.0E7, 3.0E7);
                        last_y = y;
                        last_z = MathHelper.clamp(z, -3.0E7, 3.0E7);
                        if (SET_SELF) {
                            player.prevX = getPositionLong(last_x);
                            player.prevY = getPositionLong(last_y);
                            player.prevZ = getPositionLong(last_z);
                        }

                        onGround = false;
                        teleportID = packet.getTeleportId();
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
            player.prevX = getPositionLong(last_x);
            player.prevY = getPositionLong(last_y);
            player.prevZ = getPositionLong(last_z);
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
       //         null,
       //         entity.getCollisionShape(),
       //         false) == null;
        return false;
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
     * spoofing the position, and it shouldn't
     * be spoofed by others.
     *
     * @return <tt>true</tt> if blocking.
     */
    public boolean isBlocking()
    {
        return blocking;
    }

    /**
     * No more EntityTracker so I put this here.
     * @param value
     * @return
     */
    private long getPositionLong(double value) {
        return MathHelper.lfloor(value * 4096.0);
    }

}
