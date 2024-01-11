package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.impl.core.ducks.network.IEntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntitySpawnS2CPacket.class)
public abstract class MixinEntitySpawnS2CPacket implements IEntitySpawnS2CPacket {
    @Unique
    private boolean attacked;

    @Override
    public void setAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    @Override
    public boolean isAttacked() {
        return attacked;
    }

}
