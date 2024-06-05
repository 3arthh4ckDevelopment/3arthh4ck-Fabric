package me.earth.earthhack.impl.core.mixins.gui.util;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisconnectedScreen.class)
public interface IDisconnectedScreen
{
    @Accessor(value = "parent")
    Screen getParentScreen();

    @Accessor(value = "reason")
    Text getReason();

    @Accessor(value = "buttonLabel")
    Text getMessage();
}
