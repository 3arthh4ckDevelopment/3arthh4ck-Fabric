package me.earth.earthhack.impl.modules.combat.anvilaura;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.anvilaura.modes.AnvilMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Box;

import java.awt.*;

final class ListenerRender extends ModuleListener<AnvilAura, Render3DEvent>
{
    public ListenerRender(AnvilAura module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        Box mineBB = module.mineBB;
        if (module.mineESP.getValue() && mineBB != null)
        {
            long time = module.mineTimer.getTime();
            double factor = MathUtil.clamp(1.0 - (time / 1000.0), 0.0, 1.0);
            if (factor != 0.0)
            {
                Color b = module.box.getValue();
                Color c = module.outline.getValue();

                b = new Color(b.getRed(),
                              b.getGreen(),
                              b.getBlue(),
                              (int) (b.getAlpha() * factor));

                c = new Color(c.getRed(),
                              c.getGreen(),
                              c.getBlue(),
                              (int) (c.getAlpha() * factor));

                Box ib = Interpolation.interpolateAxis(mineBB);
                RenderUtil.renderBox(event.getStack(), ib,
                        b, c, module.lineWidth.getValue());
            }
        }

        if (module.mode.getValue() == AnvilMode.Render
                && (!module.holdingAnvil.getValue()
                    || InventoryUtil.isHolding(Blocks.ANVIL)))
        {
            for (Box bb : module.renderBBs)
            {
                Box ib = Interpolation.interpolateAxis(bb);
                RenderUtil.renderBox(event.getStack(), ib,
                                     module.box.getValue(),
                                     module.outline.getValue(),
                                     module.lineWidth.getValue());
            }
        }
    }

}
