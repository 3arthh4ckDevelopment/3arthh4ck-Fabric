package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.BlockLagRotate;
import me.earth.earthhack.impl.modules.movement.blocklag.mode.OffsetMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceResult;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

final class ListenerMotion extends ModuleListener<BlockLag, MotionUpdateEvent> {

    public ListenerMotion(BlockLag module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.PRE
                && module.isInsideBlock()) {
            if (module.bypass.getValue()) {
                event.setY(event.getY() - module.bypassOffset.getValue());
                event.setOnGround(false);
            }
        }

        if (!module.timer.passed(module.delay.getValue())
                || !module.stage.getValue().shouldBlockLag(event.getStage())) {
            return;
        }

        if (module.wait.getValue()) {
            BlockPos currentPos = module.getPlayerPos();
            if (!currentPos.equals(module.startPos)) {
                module.disable();
                return;
            }
        }

        if (module.isInsideBlock()) {
            return;
        }

        PlayerEntity rEntity = mc.player;
        if (BlockLag.FREECAM.isEnabled()) {
            if (!module.freecam.getValue()) {
                module.disable();
                return;
            }

            rEntity = BlockLag.FREECAM.get().getPlayer();
            if (rEntity == null) {
                rEntity = mc.player;
            }
        }

        BlockPos pos = PositionUtil.getPosition(rEntity);
        if (!mc.world.getBlockState(pos).isReplaceable()) {
            if (!module.wait.getValue()) {
                module.disable();
            }

            return;
        }

        BlockPos posHead = PositionUtil.getPosition(rEntity).up(2);
        if (!mc.world.getBlockState(posHead).isReplaceable()
                && module.wait.getValue()) {
            return;
        }

        PlayerInteractEntityC2SPacket attacking = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
        for (Entity entity : mc.world.getEntitiesByClass(
                        Entity.class, new Box(pos), e -> true)) {
            if (entity != null
                    && !rEntity.equals(entity)
                    && !mc.player.equals(entity)
                    && !EntityUtil.isDead(entity)
                    /*&& entity.preventEntitySpawning*/) {
                if (entity instanceof EndCrystalEntity
                        && module.attack.getValue()
                        && Managers.SWITCH.getLastSwitch() >= module.cooldown.getValue()) {
                    float damage = DamageUtil.calculate(entity, mc.player);
                    if (damage < currentDmg) {
                        currentDmg = damage;
                        if (module.pop.getValue()
                                .shouldPop(damage, module.popTime.getValue())) {
                            attacking = PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking());
                            continue;
                        }
                    }

                    crystals = true;
                    continue;
                }

                if (!module.wait.getValue()) {
                    module.disable();
                }

                return;
            }
        }

        int weaknessSlot = -1;
        if (crystals) {
            if (attacking == null) {
                if (!module.wait.getValue()) {
                    module.disable();
                }

                return;
            }

            if (!DamageUtil.canBreakWeakness(true)) {
                if (!module.antiWeakness.getValue()
                        || module.cooldown.getValue() != 0
                        || (weaknessSlot = DamageUtil.findAntiWeakness()) == -1) {
                    if (!module.wait.getValue()) {
                        module.disable();
                    }

                    return;
                }
            }
        }

        if(module.offsetMode.getValue() == OffsetMode.Motion
                && mc.player != null
                && mc.world != null) {

            mc.player.jump();
            module.jumpTimer.reset();

            if (module.useBlink.getValue()) {

                if (module.jumpTimer.passed(295)) {
                    mc.player.jump();
                    BlockLag.BLINK.enable();
                    module.blinkTimer.reset();
                    mc.player.setVelocity(mc.player.getVelocity().getX(), module.motionAmount.getValue(), mc.player.getVelocity().getZ());
                }

                if(module.blinkTimer.passed(module.blinkDuration.getValue())
                        && module.autoDisableBlink.getValue())
                    BlockLag.BLINK.disable();
            }
        }


        if (!module.allowUp.getValue()) {
            BlockPos upUp = pos.up(2);
            BlockState upState = mc.world.getBlockState(upUp);
            if (upState.blocksMovement()) // Check if full BB?
            {
                if (!module.wait.getValue()) {
                    module.disable();
                }

                return;
            }
        }

        if(module.offsetMode.getValue() == OffsetMode.NewBypass) {
            mc.player.jump();
            event.setOnGround(true);
            BlockLag.BLINK.enable();
            for(int i = 0; i < 20; i++) {
                event.setY(event.getY() + 0.2);
                NetworkUtil.sendPacketNoEvent(new PlayerMoveC2SPacket.PositionAndOnGround(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, event.isOnGround()));
            }
            BlockLag.BLINK.disable();
            event.setY(mc.player.getY() - module.vClip.getValue());
        }

        int slot = module.anvil.getValue()
            ? InventoryUtil.findHotbarBlock(Blocks.ANVIL)
            : module.beacon.getValue()
                ? InventoryUtil.findHotbarBlock(Blocks.BEACON)
                : (module.echest.getValue()
                        || mc.world.getBlockState(pos.down())
                                   .getBlock() == Blocks.ENDER_CHEST
                    ? InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST,
                                                    Blocks.OBSIDIAN)
                    : InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN,
                                                    Blocks.ENDER_CHEST));
        if (slot == -1)
        {
            ModuleUtil.disableRed(module, "No Block found!");
            return;
        }

        Direction f = BlockUtil.getFacing(pos);
        if (f == null)
        {
            if (!module.wait.getValue())
            {
                module.disable();
            }

            return;
        }

        double y = module.applyScale(module.getY(rEntity, module.offsetMode.getValue()));
        if (Double.isNaN(y)) {
            return;
        }

        BlockPos on = pos.offset(f);
        float[] r =
                RotationUtil.getRotations(on, f.getOpposite(), rEntity);
        RayTraceResult result =
                RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], rEntity);

        float[] vec = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);
        boolean sneaking = !SpecialBlocks.shouldSneak(on, true);

        PlayerEntity finalREntity = rEntity;
        int finalWeaknessSlot = weaknessSlot;
        PlayerInteractEntityC2SPacket finalAttacking = attacking;
        if (module.singlePlayerCheck(pos))
        {
            if (!module.wait.getValue() || module.placeDisable.getValue())
                module.disable();
            return;
        }

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            int lastSlot = mc.player.getInventory().selectedSlot;
            if (module.attackBefore.getValue() && finalAttacking != null) {
                module.attack(finalAttacking, finalWeaknessSlot);
            }

            if (module.conflict.getValue() || module.rotate.getValue() != BlockLagRotate.None) {
                if (module.rotate.getValue() == BlockLagRotate.Packet) {
                    if (finalREntity.getPos().toVector3f()
                            .equals(Managers.POSITION.getVecF())) {
                        PacketUtil.doRotation(r[0], r[1], true);
                    } else {
                        PacketUtil.doPosRot(finalREntity.getX(),
                                finalREntity.getY(),
                                finalREntity.getZ(),
                                r[0],
                                r[1],
                                true);
                    }
                } else if (module.rotate.getValue() == BlockLagRotate.Motion){
                    if (finalREntity.getPos().toVector3f()
                            .equals(Managers.POSITION.getVecF())) {
                        event.setYaw(r[0]);
                        event.setPitch(r[1]);
                    } else {
                        event.setYaw(r[0]);
                        event.setPitch(r[1]);
                        PacketUtil.doPosition(finalREntity.getX(),
                                finalREntity.getY(),
                                finalREntity.getZ(),
                                true);
                    }
                } else {
                    PacketUtil.doPosition(finalREntity.getX(),
                            finalREntity.getY(),
                            finalREntity.getZ(),
                            true);
                }
            }

            PacketUtil.doY(
                    finalREntity, finalREntity.getY() + 0.42, module.onGround.getValue());
            PacketUtil.doY(
                    finalREntity, finalREntity.getY() + 0.75, module.onGround.getValue());
            PacketUtil.doY(
                    finalREntity, finalREntity.getY() + 1.01, module.onGround.getValue());
            PacketUtil.doY(
                    finalREntity, finalREntity.getY() + 1.16, module.onGround.getValue());

            /*if (module.highBlock.getValue())
            {
                PacketUtil.doY(
                        finalREntity, finalREntity.posY + 1.25, module.onGround.getValue());
            }*/

            if (!module.attackBefore.getValue() && finalAttacking != null) {
                module.attack(finalAttacking, finalWeaknessSlot);
            }

            InventoryUtil.switchTo(slot);

            if (!sneaking) {
                /*
                PingBypass.sendToActualServer(
                        new CPacketEntityAction(mc.player,
                                CPacketEntityAction.Action.START_SNEAKING));
                                //TODO: pb
                 */
            }

            PacketUtil.place(on, f.getOpposite(), slot, vec[0], vec[1], vec[2]);

            if (module.highBlock.getValue()) {
                PacketUtil.doY(
                        finalREntity, finalREntity.getY() + 1.67, module.onGround.getValue());
                PacketUtil.doY(
                        finalREntity, finalREntity.getY() + 2.01, module.onGround.getValue());
                /*PacketUtil.doY(
                        finalREntity, finalREntity.posY + 2.25, module.onGround.getValue());*/
                PacketUtil.doY(
                        finalREntity, finalREntity.getY() + 2.42, module.onGround.getValue());
                BlockPos highPos = pos.up();
                Direction face = Direction.DOWN;
                PacketUtil.place(highPos.offset(face), face.getOpposite(), slot, vec[0], vec[1], vec[2]);
                /*PacketUtil.doY(
                        finalREntity, finalREntity.posY + 2.84, module.onGround.getValue());*/
            }

            PacketUtil.swing(slot);

            InventoryUtil.switchTo(lastSlot);
        });

        if (!sneaking) {
            /*
            PingBypass.sendToActualServer(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.STOP_SNEAKING));
                            //TODO: pb
             */
        }

        PacketUtil.doY(rEntity, y, false);
        module.timer.reset();
        if (!module.wait.getValue() || module.placeDisable.getValue())
            module.disable();
    }
}