package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.util.interfaces.Globals;

public class WorldRenderUtil implements Globals
{
    public static void reload(boolean soft)
    {
        if (soft)
        {
            int x = (int) mc.player.getX();
            int y = (int) mc.player.getY();
            int z = (int) mc.player.getZ();
            int d = mc.options.getViewDistance().getValue() * 16;
            mc.worldRenderer.scheduleBlockRenders(
                    x - d, y - d, z - d, x + d, y + d, z + d);
            return;
        }

        mc.worldRenderer.reload();
    }

}
