package me.earth.earthhack.impl.commands.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
public class YesNoNonPausing extends RealmsLongConfirmationScreen
{
    /**
     *  Calls super constructor
     *  {@link RealmsLongConfirmationScreen#RealmsLongConfirmationScreen(BooleanConsumer callback, Type type, Text line2, Text line3, boolean yesNoQuestion)}.
     */
    public YesNoNonPausing(BooleanConsumer parentScreenIn,
                           Type type,
                           Text messageLine1In,
                           Text messageLine2In,
                           boolean yesNoQuestion)
    {
        super(parentScreenIn,
                type,
                messageLine1In,
                messageLine2In,
                yesNoQuestion);
    }
}
