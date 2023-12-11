package me.earth.earthhack.impl.modules.player.freecam;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import net.minecraft.entity.player.PlayerEntity;

public class Freecam extends DisablingModule
{
    public Freecam()
    {
        super("Freecam", Category.Player);
    }

    public PlayerEntity getPlayer() {
        return mc.player;
    }
}
