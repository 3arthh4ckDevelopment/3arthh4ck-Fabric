package me.earth.earthhack.impl.modules.render.heaven;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.client.SimpleData;

public class Heaven extends Module
{
    public Heaven()
    {
        super("Heaven", Category.Render);
        this.setShown(false);
        this.listeners.add(new ListenerUpdateCamera(this));
        this.setData(new SimpleData(this, "Sends you straight to god. @St4ro"));
    }
}
