package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IDHelper extends SubscriberImpl implements Globals
{
    private static final ScheduledExecutorService THREAD;

    static
    {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("ID-Helper");
    }

    private final Setting<Boolean> basePlaceOnly;
    private volatile int highestID;
    private boolean updated;

    public IDHelper(Setting<Boolean> basePlaceOnly)
    {
        this.basePlaceOnly = basePlaceOnly;
        this.listeners.add(new ReceiveListener<>(EntitySpawnS2CPacket.class,
            event -> checkID(event.getPacket().getEntityId())));
        this.listeners.add(new ReceiveListener<>(ExperienceOrbSpawnS2CPacket.class,
            event -> checkID(event.getPacket().getEntityId())));
        // this.listeners.add(new ReceiveListener<>(SPacketSpawnPlayer.class,
        //     event -> checkID(event.getPacket().getEntityID())));
        // this.listeners.add(new ReceiveListener<>(SPacketSpawnGlobalEntity.class,
        //     event -> checkID(event.getPacket().getEntityId())));
        this.listeners.add(new ReceiveListener<>(EntitySpawnS2CPacket.class,
            event -> checkID(event.getPacket().getEntityId())));
        // this.listeners.add(new ReceiveListener<>(SPacketSpawnMob.class,
        //     event -> checkID(event.getPacket().getEntityID())));
    }

    public int getHighestID()
    {
        return highestID;
    }

    public void setHighestID(int id)
    {
        this.highestID = id;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(boolean updated)
    {
        this.updated = updated;
    }

    public void update()
    {
        int highest = getHighestID();
        for (Entity entity : mc.world.getEntities())
        {
            if (entity.getId() > highest)
            {
                highest = entity.getId();
            }
        }
        // check one more time in case a packet
        // changed this. kinda bad but whatever
        if (highest > highestID)
        {
            highestID = highest;
        }
    }

    public boolean isSafe(List<PlayerEntity> players,
                          boolean holdingCheck,
                          boolean toolCheck)
    {
        if (!holdingCheck)
        {
            return true;
        }

        for (PlayerEntity player : players)
        {
            if (isDangerous(player, true, toolCheck))
            {
                return false;
            }
        }

        return true;
    }

    public boolean isDangerous(PlayerEntity player,
                               boolean holdingCheck,
                               boolean toolCheck)
    {
        if (!holdingCheck)
        {
            return false;
        }

        return InventoryUtil.isHolding(player, Items.BOW)
            || InventoryUtil.isHolding(player, Items.EXPERIENCE_BOTTLE)
            || toolCheck && (
               player.getMainHandStack().getItem() instanceof PickaxeItem
                || player.getMainHandStack().getItem() instanceof ShovelItem);
    }

    public void attack(SwingTime breakSwing,
                       PlaceSwing godSwing,
                       int idOffset,
                       int packets,
                       int sleep)
    {

        if (basePlaceOnly.getValue())
        {
            return;
        }

        if (sleep <= 0)
        {
            attackPackets(breakSwing, godSwing, idOffset, packets);
        }
        else
        {
            THREAD.schedule(() -> {
                    update();
                    attackPackets(breakSwing, godSwing, idOffset, packets);
                },
                sleep,
                TimeUnit.MILLISECONDS);
        }
    }

    private void attackPackets(SwingTime breakSwing,
                               PlaceSwing godSwing,
                               int idOffset,
                               int packets)
    {
        for (int i = 0; i < packets; i++)
        {
            int id = highestID + idOffset + i;
            Entity entity = mc.world.getEntityById(id);
            if (entity == null || entity instanceof EndCrystalEntity)
            {
                if (godSwing == PlaceSwing.Always
                        && breakSwing == SwingTime.Pre)
                {
                    Swing.Packet.swing(Hand.MAIN_HAND);
                }

                PlayerInteractEntityC2SPacket packet = PacketUtil.attackPacket(id);
                mc.player.networkHandler.sendPacket(packet);

                if (godSwing == PlaceSwing.Always
                        && breakSwing == SwingTime.Post)
                {
                    Swing.Packet.swing(Hand.MAIN_HAND);
                }
            }
        }

        if (godSwing == PlaceSwing.Once)
        {
            Swing.Packet.swing(Hand.MAIN_HAND);
        }
    }

    private void checkID(int id)
    {
        if (id > highestID)
        {
            highestID = id;
        }
    }

}
