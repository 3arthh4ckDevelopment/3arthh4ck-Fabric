package me.earth.earthhack.impl.modules.combat.autocrystal.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.List;

public interface IBreakHelper<T extends CrystalData>
{
    BreakData<T> newData(Collection<T> data);

    BreakData<T> getData(Collection<T> dataSet,
                         List<Entity> entities,
                         List<PlayerEntity> players,
                         List<PlayerEntity> friends);
}
