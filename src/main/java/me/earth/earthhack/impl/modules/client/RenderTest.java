package me.earth.earthhack.impl.modules.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;

public class RenderTest extends Module {

    public RenderTest() {
        super("RenderTest", Category.Client);

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
            if (mc.player == null || mc.world == null) {
                return;
            }
        }));

        this.listeners.add(new LambdaListener<>(Render2DEvent.class, event -> {
            if (mc.player == null || mc.world == null) {
                return;
            }
        }));

        this.listeners.add(new LambdaListener<>(TickEvent.class, event -> {
            if (mc.player == null || mc.world == null) {
                return;
            }
        }));
    }
}
