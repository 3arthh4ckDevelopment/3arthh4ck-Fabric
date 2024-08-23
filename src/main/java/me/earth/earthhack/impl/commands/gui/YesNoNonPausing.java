package me.earth.earthhack.impl.commands.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
public class YesNoNonPausing extends RealmsConfirmScreen
{
    /**
     *  Calls super constructor
     *  {@link RealmsConfirmScreen#RealmsConfirmScreen(BooleanConsumer callback, Text title1, Text title2)}.
     */
    public YesNoNonPausing(BooleanConsumer parentScreenIn,
                           Text messageLine1In,
                           Text messageLine2In)
    {
        super(parentScreenIn,
                messageLine1In,
                messageLine2In);
    }
}
