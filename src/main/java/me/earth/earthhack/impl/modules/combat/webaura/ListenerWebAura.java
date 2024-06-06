package me.earth.earthhack.impl.modules.combat.webaura;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.blocks.noattack.NoAttackObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class ListenerWebAura extends NoAttackObbyListener<WebAura>
{
    public ListenerWebAura(WebAura module)
    {
        super(module, -10);
    }

    @Override
    protected TargetResult getTargets(TargetResult result)
    {
        switch (module.target.getValue())
        {
            case Closest:
                module.currentTarget = EntityUtil.getClosestEnemy();
                if (module.currentTarget == null
                        || module.currentTarget.squaredDistanceTo(mc.player)
                        > MathUtil.square(module.targetRange.getValue()))
                {
                    return result.setValid(false);
                }

                return trap(module.currentTarget, result);
            case Untrapped:
                module.currentTarget = null;
                List<PlayerEntity> players = new ArrayList<>();
                for (PlayerEntity player : mc.world.getPlayers())
                {
                    if (player == null
                            || EntityUtil.isDead(player)
                            || Managers.FRIENDS.contains(player)
                            || player.equals(mc.player))
                    {
                        continue;
                    }

                    BlockPos pos = new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB
                            || mc.world.getBlockState(pos.up())
                            .getBlock() == Blocks.COBWEB)
                    {
                        continue;
                    }

                    if (mc.player.squaredDistanceTo(player)
                            < MathUtil.square(module.targetRange.getValue()))
                    {
                        players.add(player);
                    }
                }

                players.sort(Comparator.comparingDouble(p ->
                        p.squaredDistanceTo(mc.player)));

                for (PlayerEntity player : players)
                {
                    trap(player, result);
                }

                return result;
            default:
                return result.setValid(false);
        }
    }

    @Override
    protected int getSlot()
    {
        return InventoryUtil.findHotbarBlock(Blocks.COBWEB);
    }

    @Override
    protected String getDisableString()
    {
        return "Disabled, no Webs.";
    }

    private TargetResult trap(Entity entity, TargetResult result)
    {
        BlockPos pos = new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ());
        BlockPos up  = pos.up();
        BlockState state   = mc.world.getBlockState(pos);
        BlockState upState = mc.world.getBlockState(up);

        if (state.getBlock() == Blocks.COBWEB
                || upState.getBlock() == Blocks.COBWEB)
        {
            return result;
        }

        if (state.isReplaceable())
        {
            result.getTargets().add(pos);
        }
        else if (upState.isReplaceable())
        {
            result.getTargets().add(up);
        }

        return result;
    }

}