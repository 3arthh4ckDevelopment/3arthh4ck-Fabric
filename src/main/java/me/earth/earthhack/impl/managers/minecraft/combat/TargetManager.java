package me.earth.earthhack.impl.managers.minecraft.combat;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

//TODO: implement the modules for this

public class TargetManager
{
    private static final ModuleCache<AutoCrystal> AUTO_CRYSTAL =
            Caches.getModule(AutoCrystal.class);
    private static final ModuleCache<KillAura> KILL_AURA =
            Caches.getModule(KillAura.class);
    //private static final ModuleCache<AutoTrap> AUTO_TRAP =
      //      Caches.getModule(AutoTrap.class);

    public Entity getKillAura()
    {
        return null;
        // return KILL_AURA.returnIfPresent(KillAura::getTarget, null);
    }

    public PlayerEntity getAutoTrap()
    {
        return null;
        // return AUTO_TRAP.returnIfPresent(AutoTrap::getTarget, null);
    }

    public PlayerEntity getAutoCrystal()
    {
        return null;
        // return AUTO_CRYSTAL.returnIfPresent(AutoCrystal::getTarget, null);
    }

    public Entity getCrystal()
    {
        return null;
        // return AUTO_CRYSTAL.returnIfPresent(AutoCrystal::getCrystal, null);
    }

}
