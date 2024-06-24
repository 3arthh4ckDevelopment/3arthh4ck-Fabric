package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import net.minecraft.util.Hand;

public enum SwingType
{
    None
    {
        @Override
        public Hand getHand()
        {
            return null;
        }
    },
    MainHand
    {
        @Override
        public Hand getHand()
        {
            return Hand.MAIN_HAND;
        }
    },
    OffHand
    {
        @Override
        public Hand getHand()
        {
            return Hand.OFF_HAND;
        }
    };

    public static final String DESCRIPTION =
            """
                    - None : will not swing clientsided.
                    - MainHand : Swings with your MainHand.
                    - OffHand : will swing with your Offhand.""";


    public abstract Hand getHand();

}
