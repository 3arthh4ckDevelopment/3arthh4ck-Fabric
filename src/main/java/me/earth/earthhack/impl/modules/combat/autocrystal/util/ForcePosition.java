package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Set;

public class ForcePosition extends PositionData
{
    private final PositionData data;

    public ForcePosition(PositionData data,
                         AutoCrystal module)
    {
        super(data.getPos(), data.getMaxLength(), module);
        this.data = data;
    }

    public PositionData getData()
    {
        return data;
    }

    @Override
    public boolean usesObby()
    {
        return data.usesObby();
    }

    @Override
    public float getMaxDamage()
    {
        return data.getMaxDamage();
    }

    @Override
    public void setDamage(float damage)
    {
        data.setDamage(damage);
    }

    @Override
    public float getSelfDamage()
    {
        return data.getSelfDamage();
    }

    @Override
    public void setSelfDamage(float selfDamage)
    {
        data.setSelfDamage(selfDamage);
    }

    @Override
    public PlayerEntity getTarget()
    {
        return data.getTarget();
    }

    @Override
    public void setTarget(PlayerEntity target)
    {
        data.setTarget(target);
    }

    @Override
    public PlayerEntity getFacePlacer()
    {
        return data.getFacePlacer();
    }

    @Override
    public void setFacePlacer(PlayerEntity facePlace)
    {
        data.setFacePlacer(facePlace);
    }

    @Override
    public Set<PlayerEntity> getAntiTotems()
    {
        return data.getAntiTotems();
    }

    @Override
    public void addAntiTotem(PlayerEntity player)
    {
        data.addAntiTotem(player);
    }

    @Override
    public boolean isBlocked()
    {
        return data.isBlocked();
    }

    @Override
    public float getMinDiff()
    {
        return data.getMinDiff();
    }

    @Override
    public void setMinDiff(float minDiff)
    {
        data.setMinDiff(minDiff);
    }

    @Override
    public boolean isForce()
    {
        return true;
    }

    @Override
    public void addForcePlayer(PlayerEntity player)
    {
        data.addForcePlayer(player);
    }

    @Override
    public boolean isLiquid()
    {
        return data.isLiquid();
    }

    @Override
    public int compareTo(PositionData o)
    {
        if (o instanceof ForcePosition)
        {
            int c = Float.compare(this.getMinDiff(), o.getMinDiff());
            if (c != 0)
            {
                return c;
            }
        }

        return super.compareTo(o);
    }

}
