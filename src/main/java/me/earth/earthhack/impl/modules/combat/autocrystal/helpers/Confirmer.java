package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class Confirmer extends SubscriberImpl
{
    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch breakTimer = new StopWatch();

    private BlockPos current;
    private Box bb;
    private boolean placeConfirmed;
    private boolean breakConfirmed;
    private boolean newVer;
    private boolean valid;
    private int placeTime;

    public Confirmer()
    {
        this.listeners.add(new ReceiveListener<>(EntitySpawnS2CPacket.class, e ->
        {
            EntitySpawnS2CPacket p = e.getPacket();
            ChatUtil.sendMessage(String.format("Confirmer received serverbound EntitySpawn: {%s, pos(%s, %s, %s)}",
                    p.getEntityData(), p.getX(), p.getY(), p.getZ()));

            if (p.getEntityData() == 51)
            {
                confirmPlace(p.getX(), p.getY(), p.getZ());
            }
        }));

        this.listeners.add(new ReceiveListener<>(PlaySoundS2CPacket.class, e ->
        {
            PlaySoundS2CPacket p = e.getPacket();
            if (p.getCategory() == SoundCategory.BLOCKS
                && p.getSound() == Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_GENERIC_EXPLODE.value()))
            {
                confirmBreak(p.getX(), p.getY(), p.getZ());
            }
        }));
    }

    public void setPos(BlockPos pos, boolean oldVer, int placeTime)
    {
        this.newVer = oldVer;

        if (pos == null)
        {
            this.current = null;
            this.valid = false;
        }
        else
        {
            BlockPos crystalPos = BlockPos.ofFloored(pos.getX() + 0.5f,
                                               pos.getY() + 1,
                                               pos.getZ() + 0.5f);
            this.current = crystalPos;
            this.bb = createBB(crystalPos, oldVer);
            this.valid = true;
            this.placeConfirmed = false;
            this.breakConfirmed = false;
            this.placeTime = placeTime < 50 ? 0 : placeTime;
            this.placeTimer.reset();
        }
    }

    public void confirmPlace(double x, double y, double z)
    {
        if ( valid && !placeConfirmed)
        {
            BlockPos p = BlockPos.ofFloored(x, y, z);
            if (p.equals(current))
            {
                placeConfirmed = true;
                breakTimer.reset();
            }
            else if (placeTimer.passed(placeTime))
            {
                Box currentBB = bb;
                if (currentBB != null
                        && currentBB.intersects(createBB(x, y, z, newVer)))
                {
                    valid = false;
                }
            }
        }
    }

    public void confirmBreak(double x, double y, double z)
    {
        if (valid && !breakConfirmed && placeConfirmed)
        {
            BlockPos current = this.current;
            if (current != null && current.getSquaredDistance(x, y, z) < 144)
            {
                if (current.equals(BlockPos.ofFloored(x, y, z)))
                {
                    breakConfirmed = true;
                }
                else
                {
                    valid = false;
                }
            }
        }
    }

    public boolean isValid()
    {
        return valid;
    }

    public boolean isPlaceConfirmed(int placeConfirm)
    {
        if (!placeConfirmed && placeTimer.passed(placeConfirm))
        {
            valid = false;
            return false;
        }

        return placeConfirmed && valid;
    }

    public boolean isBreakConfirmed(int breakConfirm)
    {
        if (placeConfirmed
                && !breakConfirmed
                && breakTimer.passed(breakConfirm))
        {
            valid = false;
            return false;
        }

        return breakConfirmed && valid;
    }

    private Box createBB(BlockPos crystalPos, boolean oldVer)
    {
        return createBB(crystalPos.getX() + 0.5f,
                        crystalPos.getY(),
                        crystalPos.getZ() + 0.5f,
                        oldVer);
    }

    private Box createBB(double x, double y, double z, boolean oldVer)
    {
        return new Box(x - 1,
                                 y,
                                 z - 1,
                                 x + 1,
                                 y + (oldVer ? 2 : 1),
                                 z + 1);
    }

    public static Confirmer createAndSubscribe(EventBus bus)
    {
        Confirmer confirmer = new Confirmer();
        bus.subscribe(confirmer);
        return confirmer;
    }

}

