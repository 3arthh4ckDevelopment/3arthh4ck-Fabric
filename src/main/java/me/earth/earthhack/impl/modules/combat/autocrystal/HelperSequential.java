package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.listeners.PostSendListener;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HelperSequential extends SubscriberImpl implements Globals {
    private final StopWatch timer = new StopWatch();
    private final AutoCrystal module;
    private volatile BlockPos expecting;
    private volatile Vec3d crystalPos;

    public HelperSequential(AutoCrystal module) {
        this.module = module;
        listeners.add(new ReceiveListener<>(BlockUpdateS2CPacket.class, e -> {
            BlockPos expected = expecting;
            if (expected != null && expected.equals(e.getPacket().getPos())) {
                if (module.antiPlaceFail.getValue() && crystalPos == null) {
                    module.placeTimer.setTime(0);
                    setExpecting(null);
                    if (module.debugAntiPlaceFail.getValue()) {
                        mc.execute(
                            () -> ModuleUtil.sendMessageWithAquaModule(
                            module, "Crystal failed to place!",
                            "antiPlaceFail"));
                    }
                }
            }
        }));
        listeners.add(new ReceiveListener<>(EntitySpawnS2CPacket.class, e -> {
            if (e.getPacket().getEntityData() == 51) {
                BlockPos pos = BlockPos.ofFloored(e.getPacket().getX(),
                                            e.getPacket().getY(),
                                            e.getPacket().getZ());
                if (pos.down().equals(expecting)) {
                    if (module.endSequenceOnSpawn.getValue()) {
                        setExpecting(null);
                    } else if (crystalPos == null) {
                        crystalPos = new Vec3d(
                            e.getPacket().getX(),
                            e.getPacket().getY(),
                            e.getPacket().getZ());
                    }
                }
            }
        }));
        listeners.add(new PostSendListener<>(PlayerInteractEntityC2SPacket.class, e -> {
            Entity entity = ((IPlayerInteractEntityC2S) e.getPacket()).getAttackedEntity();
            if (entity instanceof EndCrystalEntity) {
                if (module.endSequenceOnBreak.getValue()) {
                    setExpecting(null);
                } else {
                    crystalPos = entity.getPos();
                }
            }
        }));
        listeners.add(new ReceiveListener<>(PlaySoundS2CPacket.class, e -> {
            Vec3d cPos = crystalPos;
            if (module.endSequenceOnExplosion.getValue()
                && e.getPacket().getCategory() == SoundCategory.BLOCKS
                && e.getPacket().getSound() == Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_GENERIC_EXPLODE.value())
                && cPos != null
                && cPos.squaredDistanceTo(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ()) < 144) {
                setExpecting(null);
            }
        }));
        /*
        TODO: compatibility with mining/placing the same pos or and offset
        listeners.add(new PostSendListener<>(CPacketPlayerDigging.class, e -> {
        }));
        */
    }

    public boolean isBlockingPlacement() {
        return module.sequential.getValue()
            && expecting != null
            && !timer.passed(module.seqTime.getValue());
    }

    public void setExpecting(BlockPos expecting) {
        timer.reset();
        this.expecting = expecting;
        this.crystalPos = null;
    }

}
