package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInteractEntityC2SPacket.class)
public abstract class MixinPlayerInteractEntityC2S implements IPlayerInteractEntityC2S {

    @Unique
    private Entity entity;

    @Override
    @Accessor(value = "entityId")
    public abstract void setEntityId(int entityId);

    @Override
    @Accessor(value = "type")
    public abstract void setAction(PlayerInteractEntityC2SPacket.InteractTypeHandler action);

    @Override
    @Accessor(value = "entityId")
    public abstract int getEntityID();

    @Override
    @Accessor(value = "type")
    public abstract PlayerInteractEntityC2SPacket.InteractTypeHandler getAction();

    @Override
    public Entity getAttackedEntity() {
        return entity;
    }
}
