package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.decoration.EndCrystalEntity;

public class InstantAttackListener<M extends InstantAttackingModule>
        extends ModuleListener<M, PacketEvent.Receive<SPacketSpawnObject>>
{
    public InstantAttackListener(M module)
    {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event)
    {
        SPacketSpawnObject packet = event.getPacket();
        if (mc.player == null
            || packet.getType() != 51
            || !module.getTimer().passed(module.getBreakDelay())
            || Managers.SWITCH.getLastSwitch() < module.getCooldown()
            || DamageUtil.isWeaknessed())
        {
            return;
        }

        EndCrystalEntity crystal = new EndCrystalEntity(
                mc.world, packet.getX(), packet.getY(), packet.getZ());
        try
        {
            attack(crystal);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    protected void attack(EndCrystalEntity crystal) throws Throwable
    {
        if (!module.shouldAttack(crystal))
        {
            return;
        }

        float damage = DamageUtil.calculate(crystal,
                                            RotationUtil.getRotationPlayer());

        if (!module.getPop().shouldPop(damage, module.getPopTime()))
        {
            return;
        }

        PacketUtil.attack(crystal);
        module.postAttack(crystal);
    }

}
