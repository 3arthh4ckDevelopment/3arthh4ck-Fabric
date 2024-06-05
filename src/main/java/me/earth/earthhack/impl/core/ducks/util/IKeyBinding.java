package me.earth.earthhack.impl.core.ducks.util;

import net.minecraft.client.util.InputUtil;

public interface IKeyBinding
{
    void earthhack$setPressed(boolean pressed);
    InputUtil.Key earthhack$getBound();
}
