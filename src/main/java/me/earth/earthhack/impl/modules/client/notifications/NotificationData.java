package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.api.module.data.DefaultData;

final class NotificationData extends DefaultData<Notifications>
{
    public NotificationData(Notifications module)
    {
        super(module);
        register(module.modules, "Announces when modules get toggled.");
        register(module.configure, "Configure the which modules should be announced.");
        register(module.categories, "Click through the module categories.");
        register(module.totems, "Announces when players in visual range pop a totem.");
        register(module.totemAmountColor, "Color of the TotemPop Amount in the TotemPop Message.");
        register(module.totemColor, "Color of the TotemPop Message.");
        register(module.totemPlayerColor, "Color of the PlayerName the TotemPop Message.");
    }

    @Override
    public int getColor()
    {
        return 0xff34A1FF;
    }

    @Override
    public String getDescription()
    {
        return "Chat notifications for all sorts of stuff.";
    }

}
