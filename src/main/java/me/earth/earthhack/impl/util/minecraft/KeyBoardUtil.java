package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.core.mixins.util.IKeyBinding;
import me.earth.earthhack.pingbypass.input.Keyboard;
import net.minecraft.client.option.KeyBinding;

public class KeyBoardUtil
{
    public static boolean isKeyDown(KeyBinding binding)
    {
        return isKeyDown(((IKeyBinding) binding).getBoundKey().getCode());
    }

    public static boolean isKeyDown(Setting<Bind> setting)
    {
        return isKeyDown(setting.getValue());
    }

    public static boolean isKeyDown(Bind bind)
    {
        return isKeyDown(bind.getKey());
    }

    public static boolean isKeyDown(int key)
    {
        /* TODO Mouse.isButtonDown(key + 100) */

        return key != 0 && key != -1
                && (key < 0 || Keyboard.isKeyDown(key));
    }
}
