package me.earth.earthhack.impl.modules.movement.autosprint.mode;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.option.KeyBinding;

public enum SprintMode implements Globals
{
    Rage
            {
                @Override
                public void sprint()
                {
                    mc.player.setSprinting(true);
                }
            },
    Legit
            {
                @Override
                public void sprint()
                {
                    KeyBinding.setKeyPressed(
                            mc.options.sprintKey.getDefaultKey(),
                            true);
                }
            };

    public abstract void sprint();

}
