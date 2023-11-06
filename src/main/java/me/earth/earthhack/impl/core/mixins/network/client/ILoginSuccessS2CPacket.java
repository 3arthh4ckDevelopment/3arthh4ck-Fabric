package me.earth.earthhack.impl.core.mixins.network.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoginSuccessS2CPacket.class)
public interface ILoginSuccessS2CPacket {
    @Accessor("profile")
    void setProfile(GameProfile profile);
}
