package me.earth.earthhack.impl.modules.combat.antitrap;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antitrap.util.AntiTrapMode;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;
import java.util.List;

final class ListenerAntiTrap extends ObbyListener<AntiTrap>
{
    private static final ModuleCache<Offhand> OFFHAND =
        Caches.getModule(Offhand.class);

    public AntiTrapMode mode = AntiTrapMode.Fill;

    public ListenerAntiTrap(AntiTrap module)
    {
        super(module, 10);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.autoOff.getValue()
            && !PositionUtil.getPosition().equals(module.startPos))
        {
            module.disable();
            return;
        }

        this.mode = module.mode.getValue();
        switch (this.mode) {
            case Crystal -> doCrystal(event);
            case FacePlace, Bomb, Fill -> super.invoke(event);
            default -> { /* NOP */}
        }
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        BlockPos playerPos = PositionUtil.getPosition();
        Vec3i[] offsets = mode.getOffsets();
        for (Vec3i offset : offsets)
        {
            if (module.mode.getValue() == AntiTrapMode.Fill)
            {
                if (mc.world.getBlockState(playerPos.add(offset.getX() / 2,
                                                         0,
                                                         offset.getZ() / 2))
                            .getBlock() == Blocks.BEDROCK)
                {
                    continue;
                }
            }

            BlockPos pos = playerPos.add(offset);
            boolean pass = false;

            if (module.waitForMine.getValue()) {
                for (Direction dir : Direction.values()) {
                    for (BlockPos hitPos : module.hit)
                        pass = pos.offset(dir).equals(hitPos);
                }
            } else pass = true;

            if (module.mode.getValue() == AntiTrapMode.Fill
                    && !module.highFill.getValue()
                    && pos.getY() > playerPos.getY()
                || module.mode.getValue() == AntiTrapMode.FacePlace
                    && !module.highFacePlace.getValue()
                    && pos.getY() > playerPos.getY() + 1
                && (module.waitForMine.getValue() && pass))
            {
                continue;
            }

            result.getTargets().add(pos);
        }

        return result;
    }

    private void doCrystal(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.PRE)
        {
            List<BlockPos> positions = module.getCrystalPositions();

            if (positions.isEmpty() || !module.isEnabled())
            {
                if (!module.empty.getValue())
                {
                    module.disable();
                }

                return;
            }

            if (module.offhand.getValue())
            {
                if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
                {
                    module.previous = OFFHAND.returnIfPresent(Offhand::getMode,
                                                              null);
                    OFFHAND.computeIfPresent(o ->
                                                 o.setMode(OffhandMode.CRYSTAL));
                    return;
                }
            }
            else
            {
                module.slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);

                if (module.slot == -1)
                {
                    ModuleUtil.disable(module, TextColor.RED
                        + "No crystals found.");
                    return;
                }
            }

            PlayerEntity closest = EntityUtil.getClosestEnemy();
            if (closest != null)
            {
                positions.sort(Comparator.comparingDouble(
                    pos -> BlockUtil.getDistanceSq(closest, pos)));
            }

            // get last, furthest away, pos.
            module.pos = positions.get(positions.size() - 1);
            module.rotations = RotationUtil.getRotationsToTopMiddle(module.pos.up());
            module.result = RayTraceUtil.getBlockHitResult(module.rotations[0],
                                                           module.rotations[1],
                                                           3.0f);
            if (module.rotate.getValue() == Rotate.Normal)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }
            else
            {
                executeCrystal();
            }
        }
        else
        {
            executeCrystal();
        }
    }

    private void executeCrystal()
    {
        if (module.pos != null && module.result != null)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::executeLocked);
        }
    }

    private void executeLocked()
    {
        final int lastSlot = mc.player.getInventory().selectedSlot;
        if (!InventoryUtil.isHolding(Items.END_CRYSTAL))
        {
            if (module.offhand.getValue() || module.slot == -1)
            {
                return;
            }
            else
            {
                InventoryUtil.switchTo(module.slot);
            }
        }

        Hand hand =
            mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL
                ? Hand.OFF_HAND
                : Hand.MAIN_HAND;

        PlayerInteractBlockC2SPacket place =
            new PlayerInteractBlockC2SPacket(
                    hand,
                    module.result,
                    0);

                // module.pos,
                // module.result.getSide(),
                // hand,
                // (float) module.result.getPos().x,
                // (float) module.result.getPos().y,
                // (float) module.result.getPos().z);

        HandSwingC2SPacket swing = new HandSwingC2SPacket(hand);

        if (module.rotate.getValue() == Rotate.Packet
            && module.rotations != null)
        {
            /*PingBypass.sendToActualServer*/
            NetworkUtil.send(
                new PlayerMoveC2SPacket.LookAndOnGround(
                    module.rotations[0],
                    module.rotations[1],
                    mc.player.onGround));
        }

        mc.player.networkHandler.sendPacket(place);
        mc.player.networkHandler.sendPacket(swing);

        InventoryUtil.switchTo(lastSlot);

        if (module.swing.getValue())
        {
            Swing.Client.swing(hand);
        }

        module.disable();
    }

}
