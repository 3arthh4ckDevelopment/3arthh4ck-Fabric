package me.earth.earthhack.impl.modules.render.heaven;

import me.earth.earthhack.impl.event.events.render.UpdateCameraEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerUpdateCamera extends ModuleListener<Heaven, UpdateCameraEvent>
{
    public ListenerUpdateCamera(Heaven module) {
        super(module, UpdateCameraEvent.class);
    }

    @Override
    public void invoke(UpdateCameraEvent event)
    {
        mc.worldRenderer.reload();
    }
}
