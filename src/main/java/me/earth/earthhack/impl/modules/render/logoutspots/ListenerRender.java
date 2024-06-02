package me.earth.earthhack.impl.modules.render.logoutspots;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.logoutspots.util.LogoutSpot;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<LogoutSpots, Render3DEvent>
{
    public ListenerRender(LogoutSpots module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (module.render.getValue())
        {
            for (LogoutSpot spot : module.spots.values())
            {
                Box bb = Interpolation.interpolateAxis(spot.getBoundingBox());
                RenderUtil.startRender();

                if (module.chams.getValue()) {
                    GL11.glPushMatrix();
                    StaticModelPlayer<PlayerEntity> model = spot.getModel();
                    double x = spot.getX() - mc.getCameraEntity().getX();
                    double y = spot.getY() - mc.getCameraEntity().getY();
                    double z = spot.getZ() - mc.getCameraEntity().getZ();

                    event.getStack().translate(x, y, z);
                    event.getStack().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - model.getYaw()));

                    //GlStateManager.enableRescaleNormal();
                    event.getStack().scale(-1.0F, -1.0F, 1.0F);
                    double widthX = bb.maxX - bb.minX + 1;
                    double widthZ = bb.maxZ - bb.minZ + 1;

                    event.getStack().scale((float) widthX, (float) (bb.maxY - bb.minY), (float) widthZ);

                    event.getStack().translate(0.0F, -1.501F, 0.0F);

                    RenderUtil.color(module.fill.getValue());
                    RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                    //model.render(0.0625f);

                    RenderUtil.color(module.outline.getValue());
                    RenderSystem.lineWidth(module.lineWidth.getValue());
                    RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    //model.render(0.0625f);
                }

                if (module.box.getValue()) {
                    RenderUtil.drawOutline(event.getStack(), bb, 1.5f, module.outline.getValue());
                }

                RenderUtil.endRender();

                if (module.nametags.getValue()) {
                    String text = spot.getName()
                            + (module.time.getValue() ? " (" + MathUtil.round((System.currentTimeMillis() - spot.getTimeStamp()) / 1000.0, 1) + "s)" : "")
                            + " XYZ : "
                            + MathUtil.round(spot.getX(), 1)
                            + ", "
                            + MathUtil.round(spot.getY(), 1)
                            + ", "
                            + MathUtil.round(spot.getZ(), 1)
                            + " ("
                            + MathUtil.round(spot.getDistance(), 1)
                            + ")";

                    //RenderUtil.drawNametag(text, bb, module.scale.getValue(), module.color.getValue().hashCode());
                }
            }
        }
    }

}
