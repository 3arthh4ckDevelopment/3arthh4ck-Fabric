package me.earth.earthhack.impl.util.minecraft;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

// TODO: THIS IS CHINESE (REWRITE)
public class PlayerUtil implements Globals {
    public static final Map<Integer, PlayerEntity> FAKE_PLAYERS =
            new HashMap<>();

    public static OtherClientPlayerEntity createFakePlayerAndAddToWorld(GameProfile profile) {
        OtherClientPlayerEntity fakePlayer = createFakePlayer(profile);
        int randomID = -1000;
        while (FAKE_PLAYERS.containsKey(randomID)
                || mc.world.getEntityById(randomID) != null) {
            randomID = ThreadLocalRandom.current().nextInt(-100000, -100);
        }

        FAKE_PLAYERS.put(randomID, fakePlayer);
        mc.world.addEntity(fakePlayer);
        return fakePlayer;
    }

    public static OtherClientPlayerEntity createFakePlayer(GameProfile profile)
    {
        OtherClientPlayerEntity fakePlayer = new OtherClientPlayerEntity(mc.world, profile);

        fakePlayer.preferredHand = mc.player.preferredHand;
        fakePlayer.getInventory().clone(mc.player.getInventory()); // fakePlayer.inventory = mc.player.inventory;
        fakePlayer.setPosition(mc.player.getX(), mc.player.getBoundingBox().minY, mc.player.getZ());
        fakePlayer.setBodyYaw(mc.player.getYaw());
        fakePlayer.setPitch(mc.player.getPitch());
        fakePlayer.headYaw = mc.player.headYaw;
        fakePlayer.setOnGround(mc.player.isOnGround());
        fakePlayer.setSneaking(mc.player.isSneaking());
        fakePlayer.setHealth(mc.player.getHealth());
        fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());
        // fakePlayer.getLocationSkin();

        for (StatusEffectInstance effect : mc.player.getStatusEffects())
        {
            fakePlayer.addStatusEffect(effect);
        }

        return fakePlayer;
    }

    public static OtherClientPlayerEntity copyPlayer(PlayerEntity playerIn) {
        return copyPlayer(playerIn, true);
    }

    public static OtherClientPlayerEntity copyPlayer(PlayerEntity playerIn, boolean animations) {
        int count = playerIn.getInventory().getStack(playerIn.getInventory().selectedSlot).getCount();

        OtherClientPlayerEntity copy = new OtherClientPlayerEntity(mc.world, playerIn.getGameProfile());

        if (animations) {
            copy.setSneaking(playerIn.isSneaking());
            copy.handSwingProgress = playerIn.handSwingProgress;
            copy.handSwingTicks = playerIn.handSwingTicks;
            copy.limbAnimator = playerIn.limbAnimator;
            copy.getInventory().clone(playerIn.getInventory());
        }
        copy.preferredHand = playerIn.preferredHand;
        copy.age = playerIn.age;
        copy.setOnGround(playerIn.isOnGround());
        copy.setId(playerIn.getId());
        copy.getInventory().clone(playerIn.getInventory());
        copy.copyFrom(playerIn); //TODO: check
        return copy;
    }

    /**
     * Removes the given fakeplayer.
     *
     * @param fakePlayer the fakeplayer to remove.
     */
    public static void removeFakePlayer(PlayerEntity fakePlayer) {
        mc.execute(() -> {
            FAKE_PLAYERS.remove(fakePlayer.getId());
            fakePlayer.kill(); // setDead might be overridden
            if (mc.world != null) {
                mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.KILLED);
            }
        });
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && FAKE_PLAYERS.containsKey(entity.getId());
    }

    public static boolean isFakePlayer(int entityID) {
        return FAKE_PLAYERS.containsKey(entityID);
    }

    public static boolean isOtherFakePlayer(Entity entity) {
        return entity != null && entity.getId() < 0;
    }

    public static boolean isCreative(PlayerEntity player) {
        return player != null
                && (player.isCreative()
                    || player.isCreative());
    }

    public static BlockPos getBestPlace(BlockPos pos, PlayerEntity player) {
        final Direction facing = getSide(player, pos);
        if (facing == Direction.UP) {
            final Block block = mc.world.getBlockState(pos).getBlock();
            final Block block2 = mc.world.getBlockState(pos.offset(Direction.UP)).getBlock();
            if (block2 instanceof AirBlock && (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK)) {
                return pos;
            }
        } else {
            BlockPos blockPos = pos.offset(facing);
            final Block block = mc.world.getBlockState(blockPos).getBlock();
            final BlockPos blockPos2 = blockPos.down();
            final Block block2 = mc.world.getBlockState(blockPos2).getBlock();
            if (block instanceof AirBlock && (block2 == Blocks.OBSIDIAN || block2 == Blocks.BEDROCK)) {
                return blockPos2;
            }
        }
        return null;
    }

    public static Direction getSide(PlayerEntity player, BlockPos blockPos) {
        BlockPos playerPos = PositionUtil.getPosition(player);
        for (Direction facing : Direction.HORIZONTAL) {
            if (playerPos.offset(facing).equals(blockPos)) {
                return facing;
            }
        }
        if (playerPos.offset(Direction.UP).offset(Direction.UP).equals(blockPos)) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }


    public static boolean isInHole(PlayerEntity player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (Direction face : Direction.values()) {
            if (face == Direction.UP || face == Direction.DOWN) continue;
            if (!BlockUtil.isReplaceable(position.offset(face))) count++;
        }
        return count >= 3;
    }

    public static Direction getOppositePlayerFaceBetter(PlayerEntity player, BlockPos pos) {
        for (Direction face : Direction.HORIZONTAL) {
            BlockPos off = pos.offset(face);
            BlockPos off1 = pos.offset(face).offset(face);
            BlockPos playerOff = PositionUtil.getPosition(player);
            if (new BlockPos(off).equals(new BlockPos(playerOff))
                    || new BlockPos(off1).equals(new BlockPos(off1))) return face.getOpposite();
        }
        return null;
    }

    public static BlockPos getPlayerPos() {
        assert mc.player != null;
        return new BlockPos((int) Math.floor(mc.player.getX()), (int) Math.floor(mc.player.getY()), (int) Math.floor(mc.player.getZ()));
    }

}
