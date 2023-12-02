package me.earth.earthhack.impl.modules.client.management;

import me.earth.earthhack.api.module.data.DefaultData;

final class ManagementData extends DefaultData<Management>
{
    public ManagementData(Management management)
    {
        super(management);
        register(module.clear,
                "Clears saved TotemPops for all players.");
        register(module.logout,
                "If on: totempops get cleared when you log off.");
        register(module.friend, "If on: Friends you " +
                "automatically.");
        register(module.deathTime, "DeathTime for Modules that " +
                "use SetDead and similar stuff.");
        register(module.time, "Sets the worlds time. A value" +
                " of 0 means that time is running normally.");
        register(module.pooledScreenShots,
                "Removes the Lag from Screenshots.");
        register(management.soundRemove, "Allows you to disable" +
                " SoundRemove in the entire client." +
                " If its off it overrides all SoundRemove" +
                " settings in the client.");
    }

    @Override
    public int getColor()
    {
        return 0xff000000;
    }

    @Override
    public String getDescription()
    {
        return "Manages internal settings for 3arthh4ck,"
                + " it doesn't matter if this module is enabled or not.";
    }

}
