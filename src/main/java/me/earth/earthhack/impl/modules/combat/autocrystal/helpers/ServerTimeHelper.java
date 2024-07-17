package me.earth.earthhack.impl.modules.combat.autocrystal.helpers;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.SwingTime;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerTimeHelper extends SubscriberImpl implements Globals
{
    private static final ScheduledExecutorService THREAD;

    private final AutoCrystal module;
    private final Setting<ACRotate> rotate;
    private final Setting<SwingTime> placeSwing;
    private final Setting<Boolean> antiFeetPlace;
    private final Setting<Boolean> oldVersion;
    private final Setting<Integer> buffer;

    static
    {
        THREAD = ThreadUtil.newDaemonScheduledExecutor("Server-Helper");
    }

    public ServerTimeHelper(AutoCrystal module,
                            Setting<ACRotate> rotate,
                            Setting<SwingTime> placeSwing,
                            Setting<Boolean> antiFeetPlace,
                            Setting<Boolean> oldVersion,
                            Setting<Integer> buffer)
    {
        this.module = module;
        this.rotate = rotate;
        this.placeSwing = placeSwing;
        this.antiFeetPlace = antiFeetPlace;
        this.oldVersion = oldVersion;
        this.buffer = buffer;
    }

    public void onUseEntity(PlayerInteractEntityC2SPacket packet, Entity crystal)
    {
        // You can also check out IPlayerInteractEntityC2SPacket to access the entity better
        PlayerEntity closest;
        if (((IPlayerInteractEntityC2S) packet).getAction() == PlayerInteractEntityC2SPacket.ATTACK
                && antiFeetPlace.getValue()
                && (rotate.getValue() == ACRotate.None || rotate.getValue() == ACRotate.Break)
                && crystal instanceof EndCrystalEntity
                && (closest = EntityUtil.getClosestEnemy()) != null
                && BlockUtil.isSemiSafe(closest, true, oldVersion.getValue())
                && BlockUtil.isAtFeet(Managers.ENTITIES.getPlayers(),
                                        crystal.getBlockPos().down(), true, oldVersion.getValue()))
        {
            int intoTick = Managers.TICK.getTickTimeAdjusted();
            int sleep = Managers.TICK.getServerTickLengthMS() + Managers.TICK.getSpawnTime() + buffer.getValue() - intoTick;
            place(crystal.getBlockPos().down(), sleep);
        }
    }

    private void place(BlockPos pos,
                       int sleep)
    {
        SwingTime time = placeSwing.getValue();
        THREAD.schedule(() -> {
            if (InventoryUtil.isHolding(Items.END_CRYSTAL))
            {
                Hand hand = InventoryUtil.getHand(Items.END_CRYSTAL);
                BlockHitResult ray = RotationUtil.rayTraceTo(pos, mc.world);
                float[] f = RayTraceUtil.hitVecToPlaceVec(pos, ray.getPos());
                if (time == SwingTime.Pre)
                {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
                /*//TODO: check, why not
                1.12.2:
                                mc.player.connection.sendPacket(
                        new CPacketPlayerTryUseItemOnBlock(
                                pos, ray.sideHit, hand, f[0], f[1], f[2]));
                 */
                NetworkUtil.sendSequenced(seq -> new PlayerInteractBlockC2SPacket(hand,
                        new BlockHitResult(new Vec3d(f[0], f[1], f[2]), ray.getSide(), pos, false), seq));
                module.sequentialHelper.setExpecting(pos);

                if (time == SwingTime.Post)
                {
                    Swing.Packet.swing(hand);
                    Swing.Client.swing(hand);
                }
            }
        }, sleep, TimeUnit.MILLISECONDS);
    }

}
