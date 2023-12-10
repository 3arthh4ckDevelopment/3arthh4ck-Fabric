package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;

public class HoleFiller extends Module
{
    public HoleFiller() {
        super("HoleFiller", Category.Combat);
        this.listeners.clear();
    }
}
