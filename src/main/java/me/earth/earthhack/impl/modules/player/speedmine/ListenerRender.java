package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.modules.player.speedmine.mode.MineMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<Speedmine, Render3DEvent>
{
    public ListenerRender(Speedmine module)
    {
        super(module, Render3DEvent.class);
    }

    private Box cachedBB;

    private double smoothen(double value) {
        double f = ((2 * value) - 2);

        return value < 0.5
                ? 4 * Math.pow(value, 3)
                : 0.5 * Math.pow(f, 3) + 1;
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (!PlayerUtil.isCreative(mc.player)
                && module.esp.getValue() != ESPMode.None
                && module.bb != null) {
            if (cachedBB == null || !cachedBB.equals(module.bb)) {
                float max = Math.min(module.maxDamage, 1.0f);
                Box renderBB = module.bb;
                if (module.growRender.getValue() && max < 1.0f) {
                    double easedMax = smoothen(max);
                    renderBB = renderBB.expand(-0.5 + (module.smoothenRender.getValue() ? easedMax / 2.0 : max / 2.0));
                }
                cachedBB = Interpolation.interpolateAxis(renderBB);
            }

            if(module.getMode() == MineMode.Fast
                    && mc.world.getBlockState(module.getPos()).getBlock().equals(Blocks.AIR)
                    && module.airFastRender.getValue())
                return;

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            Box bb = cachedBB;
            module.esp.getValue().drawEsp(module, bb, Math.min(module.maxDamage, 1.0f));

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

}
