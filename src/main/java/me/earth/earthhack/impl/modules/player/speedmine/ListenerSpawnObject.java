package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.IEntitySpawnS2CPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperInstantAttack;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

final class ListenerSpawnObject
    extends ModuleListener<Speedmine, PacketEvent.Receive<EntitySpawnS2CPacket>>
{
    private static final ModuleCache<AutoCrystal> AUTOCRYSTAL =
        Caches.getModule(AutoCrystal.class);

    public ListenerSpawnObject(Speedmine module)
    {
        super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        World world = mc.world;
        ClientPlayerEntity player = mc.player;
        boolean antiAntiSilentSwitch = module.antiAntiSilentSwitch.getValue();
        if ((module.breakInstant.getValue() || antiAntiSilentSwitch)
            && world != null
            && player != null
            && event.getPacket().getEntityData() == 51
            && !((IEntitySpawnS2CPacket) event.getPacket()).isAttacked()
            && AUTOCRYSTAL.isPresent()
            && isBomberPos(event.getPacket()))
        {
            BlockPos pos = module.pos;
            Direction facing = module.facing;
            if (antiAntiSilentSwitch)
            {
                if (pos == null || facing == null)
                {
                    return;
                }

                int fastSlot = module.getFastSlot();
                if (fastSlot == -1)
                {
                    return;
                }

                boolean swap = module.swap.getValue();
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    int lastSlot = player.inventory.selectedSlot;
                    if (swap)
                    {
                        module.cooldownBypass.getValue().switchTo(fastSlot);
                    }

                    module.sendStopDestroy(pos, facing, false, false);
                    attack(world, event);

                    if (swap)
                    {
                        module.cooldownBypass.getValue().switchBack(
                            lastSlot, fastSlot);
                    }
                });

                mc.execute(
                    () -> module.postSend(module.toAir.getValue()));
            }
            else
            {
                attack(world, event);
            }
        }
    }

    private void attack(World world,
                        PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        AUTOCRYSTAL.get().bombPos = null;
        EntitySpawnS2CPacket packet = event.getPacket();
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        EndCrystalEntity entity = new EndCrystalEntity(world, x, y, z);
        HelperInstantAttack.attack(
            AUTOCRYSTAL.get(), event.getPacket(), event, entity, false, false);
    }

    private boolean isBomberPos(EntitySpawnS2CPacket packet)
    {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        return BlockPos.ofFloored(x, y, z).equals(AUTOCRYSTAL.get().bombPos);
    }

}
