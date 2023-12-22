package me.earth.earthhack.impl.commands.gui;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.gui.buttons.SimpleButton;
import net.minecraft.client.gui.screen.Screen;

public class EarthhackButton extends SimpleButton implements Globals
{
    public EarthhackButton(int buttonID, int xPos, int yPos, PressAction action)
    {
        super(buttonID, xPos, yPos,
                0, 40, 0, 60,
                action, "EarthhackButton");
    }

    @Override
    public void onPress(Screen parent, int id)
    {
        mc.setScreen(new CommandScreen(parent));
    }

}

