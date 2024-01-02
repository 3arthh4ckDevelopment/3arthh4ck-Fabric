package me.earth.earthhack.impl.modules.client.editor;

import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.gui.hud.HudEditorGui;
import me.earth.earthhack.impl.managers.Managers;

final class ListenerRender extends ModuleListener<HudEditor, Render2DEvent> {
    public ListenerRender(HudEditor module) {
        super(module, Render2DEvent.class);
    }

    @Override
    public void invoke(Render2DEvent event) {
        if (mc.player != null && mc.world != null) {
            if (module.show.getValue() && !(mc.currentScreen instanceof HudEditorGui)) {
                for (HudElement element : Managers.ELEMENTS.getRegistered()) {
                    if (element.isEnabled()) {
                        element.hudUpdate(event.getTickDelta());
                        element.hudDraw(event.getTickDelta());
                    }
                }
            }
        }
    }
}
