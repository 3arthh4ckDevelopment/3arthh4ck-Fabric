package me.earth.earthhack.impl.modules.combat.autocrystal;

import com.mojang.blaze3d.systems.RenderSystem;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RenderDamagePos;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


// TODO: Finish this up
final class ListenerRender extends ModuleListener<AutoCrystal, Render3DEvent> {
    private final Map<BlockPos, Long> fadeList = new HashMap<>();
    private static final Identifier CRYSTAL_LOCATION = new Identifier("earthhack:textures/client/crystal.png");
    private final MutableBB bb = new MutableBB();

    public ListenerRender(AutoCrystal module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        RenderDamagePos mode = module.renderDamage.getValue();

        if (module.render.getValue()
                && module.box.getValue()
                && module.fade.getValue()
                && !module.isPingBypass()) {
            for (Map.Entry<BlockPos, Long> set : fadeList.entrySet()) {
                if (module.getRenderPos() == set.getKey()) {
                    continue;
                }

                final Color boxColor = module.boxColor.getValue();
                final Color outlineColor = module.outLine.getValue();
                final float maxBoxAlpha = boxColor.getAlpha();
                final float maxOutlineAlpha = outlineColor.getAlpha();
                final float alphaBoxAmount = maxBoxAlpha / module.fadeTime.getValue();
                final float alphaOutlineAmount = maxOutlineAlpha / module.fadeTime.getValue();
                final int fadeBoxAlpha = MathHelper.clamp((int) (alphaBoxAmount * (set.getValue() + module.fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
                final int fadeOutlineAlpha = MathHelper.clamp((int) (alphaOutlineAmount * (set.getValue() + module.fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);

                RenderUtil.renderBox(event.getStack(),
                    Interpolation.interpolatePos(set.getKey(), 1.0f),
                    new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha),
                    new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha),
                    1.5f);
            }
        }

        BlockPos pos;
        if (module.render.getValue() && !module.isPingBypass() && (pos = module.getRenderPos()) != null) {
            if ((module.fadeComp.getValue() || !module.fade.getValue()) && module.box.getValue()) {
                BlockPos slide;
                if (module.slide.getValue() && (slide = module.slidePos) != null) {
                    double factor = module.slideTimer.getTime() / Math.max(1.0, module.slideTime.getValue());
                    if (factor >= 1.0) {
                        renderBoxMutable(event.getStack(), pos);
                        if (mode != RenderDamagePos.None) {
                            renderDamage(event.getStack(), pos);
                        }
                    } else {
                        double x = slide.getX() + (pos.getX() - slide.getX()) * factor;
                        double y = slide.getY() + (pos.getY() - slide.getY()) * factor;
                        double z = slide.getZ() + (pos.getZ() - slide.getZ()) * factor;
                        bb.setBB(
                            x,
                            y,
                            z,
                            x + 1,
                            y + 1,
                            z + 1);
                        Interpolation.interpolateMutable(bb);
                        RenderUtil.renderBox(event.getStack(), bb, module.boxColor.getValue(), module.outLine.getValue(), 1.5f);
                        if (mode != RenderDamagePos.None) {
                            renderDamage(event.getStack(), x + 0.5, y, z + 0.5);
                        }
                    }
                } else {
                    if (module.zoom.getValue()) {
                        double grow = (module.zoomOffset.getValue() - Math.signum(module.zoomOffset.getValue()) * module.zoomTimer.getTime() / Math.max(1.0, module.zoomTime.getValue())) / 2.0;
                        if (module.zoomOffset.getValue() <= 0.0 && grow >= 0.0 || module.zoomOffset.getValue() > 0.0 && grow <= 0.0) {
                            renderBoxMutable(event.getStack(), pos);
                        } else {
                            bb.setFromBlockPos(pos);
                            bb.growMutable(grow, grow, grow);
                            Interpolation.interpolateMutable(bb);
                            RenderUtil.renderBox(event.getStack(), bb, module.boxColor.getValue(), module.outLine.getValue(), 1.5f);
                        }
                    } else {
                        renderBoxMutable(event.getStack(), pos);
                    }

                    if (mode != RenderDamagePos.None) {
                        renderDamage(event.getStack(), pos);
                    }
                }
            }

            if (module.fade.getValue()) {
                fadeList.put(pos, System.currentTimeMillis());
            }
        }

        fadeList.entrySet().removeIf(e ->
                e.getValue() + module.fadeTime.getValue()
                        < System.currentTimeMillis());

        if (module.renderExtrapolation.getValue())
        {
            for (PlayerEntity player : mc.world.getPlayers())
            {
                MotionTracker tracker;
                if (player == null
                    || EntityUtil.isDead(player)
                    || RenderUtil.getEntity().squaredDistanceTo(player) > 200
                    || !RenderUtil.isInFrustum(player.getBoundingBox())
                    || player.equals(RotationUtil.getRotationPlayer())
                    || (tracker = module.extrapolationHelper
                                        .getTrackerFromEntity(player)) == null
                    || !tracker.active)
                {
                    continue;
                }

                Vec3d interpolation = Interpolation.interpolateEntity(player);
                double x = interpolation.x;
                double y = interpolation.y;
                double z = interpolation.z;

                double tX = tracker.getX() - Interpolation.getRenderPosX();
                double tY = tracker.getY() - Interpolation.getRenderPosY();
                double tZ = tracker.getZ() - Interpolation.getRenderPosZ();

                RenderUtil.startRender();
                RenderSystem.enableCull();
                RenderSystem.enableBlend();
                event.getStack().push();

                if (Managers.FRIENDS.contains(player))
                {
                    RenderSystem.setShaderColor(0.33333334f, 0.78431374f, 0.78431374f, 0.55f);
                }
                else
                {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }

                boolean viewBobbing = mc.options.getBobView().getValue();
                mc.options.getBobView().setValue(false);
                // ((IEntityRenderer) mc.entityRenderer)
                //      .invokeOrientCamera(event.getDelta());
                mc.options.getBobView().setValue(viewBobbing);

                RenderSystem.lineWidth(1.5f);

                // GL11.glBegin(GL11.GL_LINES);
                // GL11.glVertex3d(tX, tY, tZ);
                // GL11.glVertex3d(x, y, z);
                // GL11.glEnd();

                event.getStack().pop();
                RenderSystem.disableCull();
                RenderSystem.disableBlend();
                RenderUtil.endRender();
            }
        }
    }

    private void renderBoxMutable(MatrixStack stack, BlockPos pos) {
        bb.setFromBlockPos(pos);
        Interpolation.interpolateMutable(bb);
        RenderUtil.renderBox(
            stack,
            bb,
            module.boxColor.getValue(),
            module.outLine.getValue(),
            1.5f);
    }

    private void renderDamage(MatrixStack stack, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        // renderDamage(stack, x, y, z);
    }

    private void renderDamage(MatrixStack stack, double xIn, double yIn, double zIn) {
        double x = xIn;
        double y = yIn;
        double z = zIn;
        // renderDamage(stack, x, y, z);
    }

    /*
    private void renderDamage(MatrixStack stack, double x, double yIn, double z) {
        double y = yIn + (module.renderDamage.getValue() == RenderDamagePos.OnTop ? 1.35 : 0.5);
        String text = module.damage;
        stack.push();
        // RenderLayer.enableStandardItemLighting();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(1.0f, -1500000.0f);
        RenderSystem.disableColorLogicOp();
        RenderSystem.disableDepthTest();

        float scale = 0.016666668f * (module.renderMode.getValue() == RenderDamage.Indicator ? 0.95f : 1.3f);
        GlStateManager.translate(x - Interpolation.getRenderPosX(),
                y - Interpolation.getRenderPosY(),
                z - Interpolation.getRenderPosZ());

        // stack.something(0.0f, 1.0f, 0.0f);
        stack.translate(-mc.player.getHeadYaw(), 0.0f, 1.0f);

        stack.translate(mc.player.getPitch(),
                mc.options.thirdPersonView == 2
                        ? -1.0f
                        : 1.0f,
                0.0f,
                0.0f);

        stack.scale(-scale, -scale, scale);

        int distance = (int) Math.sqrt(mc.player.squaredDistanceTo(x, y, z));
        float scaleD = (distance / 2.0f) / (2.0f + (2.0f - 1));
        if (scaleD < 1.0f) {
            scaleD = 1;
        }

        stack.scale(scaleD, scaleD, scaleD);
        TextRenderer m = Managers.TEXT;
        stack.translate(-(m.getStringWidth(text) / 2.0), 0, 0);
        if (module.renderMode.getValue() == RenderDamage.Indicator) {
            Color clr = module.indicatorColor.getValue();
            Render2DUtil.drawUnfilledCircle(m.getStringWidth(text) / 2.0f, 0, 22.f, new Color(5, 5, 5, clr.getAlpha()).getRGB(), 5.f);
            Render2DUtil.drawCircle(m.getStringWidth(text) / 2.0f, 0, 22.f, clr.getRGB());
            m.drawString(context, text, 0, 6.0f, new Color(255, 255, 255).getRGB());
            mc.getTextureManager().bindTexture(CRYSTAL_LOCATION);
            Gui.drawScaledCustomSizeModalRect((int) (m.getStringWidth(text) / 2.0f) - 10, -17, 0, 0, 12, 12, 22, 22, 12, 12);
        } else {
            m.drawStringWithShadow(context, text, 0, 0, new Color(255, 255, 255).getRGB());
        }
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.disablePolygonOffset();
        RenderSystem.polygonOffset(1.0f, 1500000.0f);
        stack.pop();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    */

}

