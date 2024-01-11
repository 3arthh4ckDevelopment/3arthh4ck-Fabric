package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class ShieldPlayer extends PlayerEntity {
    public ShieldPlayer(World worldIn) {
        super(worldIn, MinecraftClient.getInstance().player.getBlockPos(), 180,  new GameProfile(UUID.randomUUID(), "Shield"));
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

}
