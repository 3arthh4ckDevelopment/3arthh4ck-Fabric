package me.earth.earthhack.impl.modules.client;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

public class RenderTest extends Module {

    public RenderTest() {
        super("RenderTest", Category.Client);

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
            if (mc.player != null && mc.world != null) {
                BlockPos pos = new BlockPos(mc.player.getBlockPos().down(1));
                Box bb = Interpolation.interpolatePos(pos, 1);
                RenderUtil.drawBox(event.getStack(), bb, new Color(255, 0, 0, 255));
            }
        }));
    }
}
