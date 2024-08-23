package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

final class ListenerSpawnObject extends
        ModuleListener<BlockLag, PacketEvent.Receive<EntitySpawnS2CPacket>>
{
    public ListenerSpawnObject(BlockLag module)
    {
        super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<EntitySpawnS2CPacket> event)
    {
        if (!module.instantAttack.getValue()
            || event.getPacket().getEntityData() != 51
            || mc.world == null
            || Managers.SWITCH.getLastSwitch() > module.cooldown.getValue()
            || !KeyBoardUtil.isKeyDown(module.getBind()) && !module.isEnabled()
            || DamageUtil.isWeaknessed()
            || mc.world.getBlockState(PositionUtil.getPosition(
                            RotationUtil.getRotationPlayer()).up(2))
                       .blocksMovement())
        {
            return;
        }

        PlayerEntity player = mc.player;
        if (player != null)
        {
            BlockPos pos = PositionUtil.getPosition(player);
            if (!mc.world.getBlockState(pos).isReplaceable())
            {
                return;
            }

            EndCrystalEntity crystal = new EndCrystalEntity(mc.world,
                                                event.getPacket().getX(),
                                                event.getPacket().getY(),
                                                event.getPacket().getZ());
            if (crystal.getBoundingBox()
                       .intersects(new Box(pos)))
            {
                float damage = DamageUtil.calculate(crystal);
                if (module.pop.getValue()
                              .shouldPop(damage, module.popTime.getValue()))
                {
                    PacketUtil.attack(Managers.ENTITIES.getEntity(event.getPacket().getEntityId()));
                }
            }
        }
    }

}
