package me.earth.earthhack.impl.util.minecraft.entity;

import net.minecraft.entity.player.PlayerEntity;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public interface IEntityProvider
{
    List<Entity> getEntities();

    List<PlayerEntity> getPlayers();

}
