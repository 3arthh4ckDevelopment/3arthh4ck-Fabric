package me.earth.earthhack.impl.modules.misc.nosoundlag;

import me.earth.earthhack.api.module.data.DefaultData;

final class NoSoundLagData extends DefaultData<NoSoundLag>
{
    public NoSoundLagData(NoSoundLag module)
    {
        super(module);
        register(module.armor, "Cancels the armor and elytra sounds.");
        register(module.crystals, "Cancels the crystals sounds.");
        register(module.withers, "Cancels the withers sounds.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Prevents lag caused by spamming certain sounds. " +
                "Probably not useful for modern versions, but " +
                "if you don't like the sounds then this might be useful.";
    }

}