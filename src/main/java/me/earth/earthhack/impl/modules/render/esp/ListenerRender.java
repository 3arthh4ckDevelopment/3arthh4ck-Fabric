package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListenerRender extends ModuleListener<ESP, Render3DEvent> {

    public ListenerRender(ESP module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (module.storage.getValue()) {
            drawBlockEntities(event, Managers.ENTITIES.getBlockEntities());
        }

        if (module.players.getValue()) {
            drawEntities(event, Managers.ENTITIES.getEntities());
        }
    }

    private void drawEntities(Render3DEvent event, List<Entity> playerEntities) {
        for (Entity entity : playerEntities) {
            if (entity.equals(mc.player)
                    || entity.distanceTo(mc.player) > module.range.getValue()
                    || entity instanceof EndCrystalEntity
                    || (entity instanceof FireworkRocketEntity rocket && rocket.getOwner().equals(mc.player)))
            {
                continue;
            }

            Box bb = Interpolation.interpolateAxis(entity.getBoundingBox());

            Color color = null;
            if (entity instanceof PlayerEntity player) {
                if (module.players.getValue()) {
                    color = module.playersColor.getValue();

                    if (entity.isInvisible()) {
                        color = module.invisibleColor.getValue();
                    }

                    if (PhaseUtil.isPhasing(player, module.pushMode.getValue())) {
                        color = module.phaseColor.getValue();
                    }

                    if (entity.equals(Managers.TARGET.getAutoCrystal())
                            || entity.equals(Managers.TARGET.getAutoTrap())
                            || entity.equals(Managers.TARGET.getKillAura())) {
                        color = module.targetColor.getValue();
                    }

                    if (Managers.FRIENDS.contains(player)) {
                        color = module.friendColor.getValue();
                    }
                }
            } else if (entity instanceof MobEntity) {
                if (module.monsters.getValue()) {
                    color = module.monstersColor.getValue();
                }
            } else if (entity instanceof AnimalEntity) {
                if (module.animals.getValue()) {
                    color = module.animalsColor.getValue();
                }
            } else if (entity instanceof VehicleEntity) {
                if (module.vehicles.getValue()) {
                    color = module.vehiclesColor.getValue();
                }
            } else if (entity instanceof ItemEntity item) {
                if (module.items.getValue()) {
                    bb = Interpolation.interpolateAxis(item.pos, new Box(-0.25, 0, -0.25, 0.25, 0.50, 0.25));
                    color = module.itemsColor.getValue();
                }
            } else if (module.misc.getValue()) {
                color = module.miscColor.getValue();
            }

            if (color == null) {
                continue;
            }

            switch (module.mode.getValue()) {
                case Outline:
                    RenderUtil.drawOutline(event.getStack(), bb, module.lineWidth.getValue(), color);
                    break;
                case Box:
                    RenderUtil.drawBox(event.getStack(), bb, color);
                    break;
                case BoxOutline:
                    RenderUtil.drawBox(event.getStack(), bb, color.darker());
                    RenderUtil.drawOutline(event.getStack(), bb, module.lineWidth.getValue(), color);
                    break;
            }
        }
    }

    private void drawBlockEntities(Render3DEvent event, List<BlockEntity> blockEntities) {
        List<BlockPos> skippedDoubleChests = new ArrayList<>();
        for (BlockEntity blockEntity : blockEntities) {
            if (!blockEntity.getPos().isWithinDistance(mc.player.pos, module.storageRange.getValue()) || skippedDoubleChests.contains(blockEntity.getPos())) {
                continue;
            }

            Box bb = blockEntity.getCachedState().getCollisionShape(blockEntity.getWorld(), blockEntity.getPos()).getBoundingBox();
            Box newBB = Interpolation.interpolateAxis(blockEntity.getPos(), bb);

            if (blockEntity instanceof ChestBlockEntity chest) { // both normal and trapped chests
                BlockState blockState = blockEntity.getCachedState();
                ChestType chestType = blockState.contains((Property<?>) ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
                if (chestType != ChestType.SINGLE) {
                    Direction direction = blockState.get(ChestBlock.FACING);
                    BlockPos otherPos = chestType == ChestType.LEFT ? chest.getPos().offset(direction.rotateYClockwise()) : chest.getPos().offset(direction.rotateYCounterclockwise());
                    BlockEntity otherBlockEntity = chest.getWorld().getBlockEntity(otherPos);
                    if (otherBlockEntity instanceof ChestBlockEntity otherChest) {
                        Box otherBB = otherChest.getCachedState().getCollisionShape(otherChest.getWorld(), otherChest.getPos()).getBoundingBox();
                        Box otherNewBB = Interpolation.interpolateAxis(otherChest.getPos(), otherBB);
                        newBB = newBB.union(otherNewBB);
                        skippedDoubleChests.add(otherChest.getPos());
                    }
                }
            }

            switch (module.mode.getValue()) {
                case Outline:
                    RenderUtil.drawOutline(event.getStack(), newBB, module.lineWidth.getValue(), module.colorTileEntity(blockEntity));
                    break;
                case Box:
                    RenderUtil.drawBox(event.getStack(), newBB, module.colorTileEntityInside(blockEntity));
                    break;
                case BoxOutline:
                    RenderUtil.drawBox(event.getStack(), newBB, module.colorTileEntityInside(blockEntity));
                    RenderUtil.drawOutline(event.getStack(), newBB, module.lineWidth.getValue(), module.colorTileEntity(blockEntity));
                    break;
            }
        }
    }
}