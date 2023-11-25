package me.earth.earthhack.impl.core.mixins.network.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInteractEntityC2SPacket.class)
public abstract class MixinPlayerInteractEntityC2S implements IPlayerInteractEntityC2S {
    @Override
    @Accessor(value = "entityId")
    public abstract void setEntityId(int entityId);

    @Override
    public void setAction(PlayerInteractEntityC2SPacket.InteractTypeHandler action) {

    }

    @Override
    public void setVec(Vec3d vec3d) {

    }

    @Override
    public void setHand(Hand hand) {

    }

    @Override
    @Accessor(value = "entityId")
    public abstract int getEntityID();

    @Override
    public PlayerInteractEntityC2SPacket.InteractTypeHandler getAction() {
        return null;
    }

    @Override
    public Vec3d getHitVec() {
        return null;
    }

    @Override
    public Entity getAttackedEntity() {
        return null;
    }
}
