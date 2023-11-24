package me.earth.earthhack.impl.util.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.core.mixins.network.IClientPlayNetworkHandler;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings({"unused", "ConstantConditions"})
public class PacketUtil implements Globals
{
  // public static Set<Class<? extends Packet<?>>> getAllPackets() // TODO
  // {
  //     return ((IEnumConnectionState) a.HANDSHAKING)
  //             .getStatesByClass()
  //             .keySet();
  // }

    public static void handlePosLook(EntityPositionS2CPacket packetIn,
                                     Entity entity,
                                     boolean noRotate)
    {
        handlePosLook(packetIn, entity, noRotate, false);
    }

    public static void handlePosLook(EntityPositionS2CPacket packet,
                                     Entity entity,
                                     boolean noRotate,
                                     boolean event)
    {
        double x    = packet.getX();
        double y    = packet.getY();
        double z    = packet.getZ();
        float yaw   = packet.getYaw();
        float pitch = packet.getPitch();

        entity.setPos(x, y, z);
        entity.setPitch(pitch);
        entity.setYaw(yaw);

        Packet<?> confirm = new TeleportConfirmC2SPacket(packet.getId());
        PlayerMoveC2SPacket posRot  = positionRotation(entity.getX(),
                entity.getBoundingBox()
                        .minY,
                entity.getZ(),
                yaw,
                pitch,
                false);

        if (event)
        {
            NetworkUtil.send(confirm);
            Managers.ROTATION.setBlocking(true);
            NetworkUtil.send(posRot);
            Managers.ROTATION.setBlocking(false);
        }
        else
        {
            NetworkUtil.sendPacketNoEvent(confirm);
            NetworkUtil.sendPacketNoEvent(posRot);
        }

        // might be called async
        mc.execute(PacketUtil::loadTerrain);
    }

    public static void startDigging(BlockPos pos, Direction facing)
    {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, facing));
    }

    public static void stopDigging(BlockPos pos, Direction facing)
    {
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing));
    }

    public static void loadTerrain()
    {
        // This might get called asynchronously so better be safe
        mc.execute(() ->
        {
            if (!((IClientPlayNetworkHandler) mc.getNetworkHandler())
                    .isDoneLoadingTerrain())
            {
                mc.player.prevX = mc.player.getX();
                mc.player.prevY = mc.player.getY();
                mc.player.prevZ = mc.player.getZ();
                ((IClientPlayNetworkHandler) mc.getNetworkHandler())
                        .setDoneLoadingTerrain(true);

                mc.setScreen(null);
            }
        });
    }

    /**
     * Produces a {@link PlayerActionC2SPacket} for the given id.
     *
     * @param entity the entity the packet should attack.
     * @return a packet that will attack the entity when sent.
     */
    public static PlayerInteractEntityC2SPacket attackPacket(Entity entity)
    {

        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking());
        //noinspection ConstantConditions
        ((IPlayerInteractEntityC2S) packet).setEntityId(entity.getId());
        //noinspection ConstantConditions
        ((IPlayerInteractEntityC2S) packet).setAction(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM); // TODO: ATTACK!!!!!!!!

        return packet;
    }

   //  public static void sneak(boolean sneak) TODO TODO TODO
   //  {
   //      PingBypass.sendToActualServer(
   //              new CPacketEntityAction(
   //                      mc.player,
   //                      sneak
   //                              ? EntityA.Action.START_SNEAKING
   //                              : CPacketEntityAction.Action.STOP_SNEAKING));
   //  }

    public static void attack(Entity entity)
    {
        mc.getNetworkHandler().sendPacket(
                PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
        mc.getNetworkHandler().sendPacket(
                new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    public static void swing(int slot)
    {
        mc.getNetworkHandler().sendPacket(
                new HandSwingC2SPacket(InventoryUtil.getHand(slot)));
    }

    public static void place(BlockPos on,
                             Direction facing,
                             int slot,
                             float x,
                             float y,
                             float z)
    {
        place(on, facing, InventoryUtil.getHand(slot), x, y, z);
    }

    public static void place(BlockPos on,
                             Direction facing,
                             Hand hand,
                             float x,
                             float y,
                             float z)
    {
        BlockHitResult result = new BlockHitResult(new Vec3d(x, y, z), facing, on, false);
        mc.getNetworkHandler().sendPacket(
                new PlayerInteractBlockC2SPacket(hand, result, 0));
    }

    public static void teleport(int id)
    {
        mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(id));
    }

    public static void sendAction(PlayerActionC2SPacket.Action action)
    {
        // PingBypass.sendToActualServer(
        //         new PlayerActionC2SPacket(action, mc.player.getBlockPos(), Direction.DOWN));
    }

    public static void click(int windowIdIn,
                             int actionNumberIn,
                             int slotIdIn,
                             int usedButtonIn,
                             SlotActionType modeIn,
                             ItemStack clickedItemIn)
    {
        Int2ObjectMap<ItemStack> map = new Int2ObjectArrayMap<>();
        map.put(1, clickedItemIn);

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                windowIdIn,
                actionNumberIn,
                slotIdIn,
                usedButtonIn,
                modeIn,
                clickedItemIn,
                map));
    }

    /*--------------- Utility for creating CPacketPlayers ---------------*/

    public static PlayerMoveC2SPacket onGround(boolean onGround)
    {
        return new PlayerMoveC2SPacket.OnGroundOnly(onGround);
    }

    public static PlayerMoveC2SPacket position(double x, double y, double z)
    {
        return position(x, y, z, mc.player.isOnGround());
    }

    public static PlayerMoveC2SPacket position(double x,
                                         double y,
                                         double z,
                                         boolean onGround)
    {
        return new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround);
    }

    public static PlayerMoveC2SPacket rotation(float yaw,
                                         float pitch,
                                         boolean onGround)
    {
        return new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround);
    }

    public static PlayerMoveC2SPacket positionRotation(double x,
                                                       double y,
                                                       double z,
                                                       float yaw,
                                                       float pitch,
                                                       boolean onGround)
    {
        return new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround);
    }

    /*--------------- Utility for sending CPacketPlayers ---------------*/

    public static void doY(double y, boolean onGround)
    {
        doY(mc.player, y, onGround);
    }

    public static void doY(Entity entity, double y, boolean onGround)
    {
        doPosition(entity.getX(), y, entity.getZ(), onGround);
    }

    public static void doPosition(double x,
                                  double y,
                                  double z,
                                  boolean onGround)
    {
        Packet<?> packet = position(x, y, z, onGround);
        // PingBypass.mayAuthorize(packet);
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static void doPositionNoEvent(double x,
                                         double y,
                                         double z,
                                         boolean onGround)
    {
        NetworkUtil.sendPacketNoEvent(position(x, y, z, onGround));
    }

    public static void doRotation(float yaw,
                                  float pitch,
                                  boolean onGround)
    {
        Packet<?> packet = rotation(yaw, pitch, onGround);
        // PingBypass.mayAuthorize(packet);
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static void doPosRot(double x,
                                double y,
                                double z,
                                float yaw,
                                float pitch,
                                boolean onGround)
    {
        Packet<?> packet = positionRotation(x, y, z, yaw, pitch, onGround);
        // PingBypass.mayAuthorize(packet);
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static void doPosRotNoEvent(double x,
                                       double y,
                                       double z,
                                       float yaw,
                                       float pitch,
                                       boolean onGround)
    {
        NetworkUtil.sendPacketNoEvent(
                positionRotation(x, y, z, yaw, pitch, onGround));
    }

}
