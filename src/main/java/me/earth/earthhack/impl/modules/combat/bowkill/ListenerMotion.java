package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class ListenerMotion extends ModuleListener<BowKiller, MotionUpdateEvent> {
    public ListenerMotion(BowKiller module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        module.entityDataArrayList.removeIf(e -> e.getTime() + 60000 < System.currentTimeMillis());
        if (!RotationUtil.getRotationPlayer().verticalCollision)
            return;


        if (event.getStage() == Stage.PRE) {
            module.blockUnder = isBlockUnder();
            if (module.rotate.getValue() && RotationUtil.getRotationPlayer().getActiveItem().getItem() == Items.BOW
                    && mc.options.useKey.isPressed() && module.blockUnder) {
                module.target = module.findTarget();
                if (module.target != null) {
                    float[] rotations
                            = module.rotationSmoother
                            .getRotations(RotationUtil.getRotationPlayer(),
                                    module.target,
                                    module.height.getValue(),
                                    module.soft.getValue());
                    if (rotations != null) {
                        if (module.silent.getValue()) {
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                        } else {
                            RotationUtil.getRotationPlayer().yaw = rotations[0];
                            RotationUtil.getRotationPlayer().pitch = rotations[1];
                        }
                    }
                }
            }
            if (RotationUtil.getRotationPlayer().getActiveItem().getItem() == Items.BOW) {
                if (!module.blockUnder) {
                    final int newSlot = findBlockInHotbar();
                    if (newSlot != -1) {
                        final int oldSlot = RotationUtil.getRotationPlayer().getInventory().selectedSlot;
                        RotationUtil.getRotationPlayer().getInventory().selectedSlot = newSlot;
                        placeBlock(PositionUtil.getPosition(RotationUtil.getRotationPlayer()).down(1), event);
                        RotationUtil.getRotationPlayer().getInventory().selectedSlot = oldSlot;
                    }
                }
            }
        } else {
            if (RotationUtil.getRotationPlayer().getActiveItem().getItem() != Items.BOW) {
                module.cancelling = false;
                module.packetsSent = 0;
            } else if (RotationUtil.getRotationPlayer().getActiveItem().getItem() == Items.BOW
                    && module.cancelling && module.blockUnder) {

                module.packetsSent++; // The server expects a packet to be sent every time this event is called.
                if (module.packetsSent > module.runs.getValue() * 2
                        && !module.always.getValue()
                        && module.needsMessage) {
                    ModuleUtil.sendMessage(module, TextColor.GREEN + "Charged!");
                }
            }
        }
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = RotationUtil.getRotationPlayer().getInventory().getStack(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof BlockItem) {
                final Block block = ((BlockItem) stack.getItem()).getBlock();
                if (block == Blocks.OBSIDIAN) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean canBeClicked(BlockPos pos) {
        return mc.world.canPlace(mc.world.getBlockState(pos), pos, ShapeContext.of(RotationUtil.getRotationPlayer()));
    }

    private void placeBlock(BlockPos pos, MotionUpdateEvent event) {
        for (Direction side : Direction.values()) {
            final BlockPos neighbor = pos.offset(side);
            final Direction side2 = side.getOpposite();
            if (!canBeClicked(neighbor))
                continue;
            float[] rotations = RotationUtil.getRotations(new Vec3d(0.5, 0.5, 0.5));
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(RotationUtil.getRotationPlayer(), ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), side2, neighbor, false));
            RotationUtil.getRotationPlayer().swingHand(Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(RotationUtil.getRotationPlayer(), ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            return;
        }
    }

    private boolean isBlockUnder() {
        return !(mc.world.getBlockState(PositionUtil.getPosition(RotationUtil.getRotationPlayer()).down(1)).getBlock() instanceof AirBlock);
    }
}
