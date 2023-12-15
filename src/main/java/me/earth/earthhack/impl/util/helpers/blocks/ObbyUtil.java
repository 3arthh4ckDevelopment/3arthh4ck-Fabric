package me.earth.earthhack.impl.util.helpers.blocks;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.math.path.BlockingEntity;
import me.earth.earthhack.impl.util.math.path.Pathable;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class ObbyUtil
{
    public static boolean place(ObbyModule module, Pathable path)
    {
        if (!path.isValid())
        {
            return false;
        }

        Entity target = null;
        boolean crystalFound = false;
        float maxDamage = Float.MAX_VALUE;
        for (BlockingEntity entity : path.getBlockingEntities())
        {
            if (module.attack.getValue()
                    && entity.getEntity() instanceof EndCrystalEntity)
            {
                crystalFound = true;
                float damage = DamageUtil.calculate(entity.getEntity(),
                        module.getPlayer());
                if (damage < maxDamage
                        && module
                        .pop
                        .getValue()
                        .shouldPop(damage, module.popTime.getValue()))
                {
                    maxDamage = damage;
                    target = entity.getEntity();
                }
            }
            else
            {
                return false;
            }
        }

        if (target != null)
        {
            module.attacking = PlayerInteractEntityC2SPacket.attack(target, target.isSneaking());
        }
        else if (crystalFound
                && module.blockingType.getValue() != BlockingType.Crystals)
        {
            return false;
        }

        for (Ray ray : path.getPath())
        {
            module.placeBlock(ray.getPos(),
                              ray.getFacing(),
                              ray.getRotations(),
                              ray.getResult().hitVec);

            if (module.blocksPlaced >= module.blocks.getValue()
                    || module.rotate.getValue() == Rotate.Normal)
            {
                return true;
            }
        }

        return true;
    }

}
