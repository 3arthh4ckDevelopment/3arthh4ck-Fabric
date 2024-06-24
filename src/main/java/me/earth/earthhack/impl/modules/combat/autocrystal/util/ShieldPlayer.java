package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ShieldPlayer extends PlayerEntity {
    public ShieldPlayer(World worldIn) {
        super(worldIn, BlockPos.ORIGIN, 0.0f, new GameProfile(UUID.randomUUID(), "Shield"));
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
