package me.earth.earthhack.impl.hud.visual.compass;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Compass extends HudElement {

    private final Setting<CompassMode> compass =
            register(new EnumSetting<>("Mode", CompassMode.Circle));
    private final Setting<Integer> scale =
            register(new NumberSetting<>("Scale", 3, 1, 10));
    private final Setting<Boolean> spawnLine =
            register(new BooleanSetting("SpawnLine", false));

    private void render(DrawContext context) {
        if (mc.player != null) {
            float x = getX();
            float y = getY();
            if (compass.getValue() == CompassMode.Line) {
                float playerYaw = mc.player.getBodyYaw();
                float rotationYaw = wrap(playerYaw);
                Render2DUtil.drawRect(context.getMatrices(), x, y, x + 100, y + Managers.TEXT.getStringHeight(), 0x75101010);
                glScissor(x, y, x + 100, y + Managers.TEXT.getStringHeight());
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                final float zeroZeroYaw = wrap((float) (Math.atan2(0 - mc.player.getZ(), 0 - mc.player.getX()) * 180.0d / Math.PI) - 90.0f);
                if (spawnLine.getValue())
                    Render2DUtil.drawLine(context.getMatrices(), x - rotationYaw + 50 + zeroZeroYaw, y + 2, x - rotationYaw + 50 + zeroZeroYaw, y + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFF1010);
                Render2DUtil.drawLine(context.getMatrices(), (x - rotationYaw + 50) + 45, y + 2, (x - rotationYaw + 50) + 45, y + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
                Render2DUtil.drawLine(context.getMatrices(), (x - rotationYaw + 50) - 45, y + 2, (x - rotationYaw + 50) - 45, y + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
                Render2DUtil.drawLine(context.getMatrices(), (x - rotationYaw + 50) + 135, y + 2, (x - rotationYaw + 50) + 135, y + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
                Render2DUtil.drawLine(context.getMatrices(), (x - rotationYaw + 50) - 135, y + 2, (x - rotationYaw + 50) - 135, y + Managers.TEXT.getStringHeight() - 2, 2, 0xFFFFFFFF);
                Managers.TEXT.drawStringWithShadow(context, "n", (x - rotationYaw + 50) + 180 - Managers.TEXT.getStringWidth("n") / 2.0f, y, 0xFFFFFFFF);
                Managers.TEXT.drawStringWithShadow(context, "n", (x - rotationYaw + 50) - 180 - Managers.TEXT.getStringWidth("n") / 2.0f, y, 0xFFFFFFFF);
                Managers.TEXT.drawStringWithShadow(context, "e", (x - rotationYaw + 50) - 90 - Managers.TEXT.getStringWidth("e") / 2.0f, y, 0xFFFFFFFF);
                Managers.TEXT.drawStringWithShadow(context, "s", (x - rotationYaw + 50) - Managers.TEXT.getStringWidth("s") / 2.0f, y, 0xFFFFFFFF);
                Managers.TEXT.drawStringWithShadow(context, "w", (x - rotationYaw + 50) + 90 - Managers.TEXT.getStringWidth("w") / 2.0f, y, 0xFFFFFFFF);
                Render2DUtil.drawLine(context.getMatrices(), (x + 50), y + 1, (x + 50), y + Managers.TEXT.getStringHeight() - 1, 2, 0xFF909090);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            } else {
                x += getWidth() / 2;
                y += getHeight() / 2;
                for (Direction dir : Direction.values()) {
                    double rad = getPosOnCompass(dir);
                    Managers.TEXT.drawStringWithShadow(context, dir.name(), (float) (x + getCompassX(rad)), (float) (y + getCompassY(rad)), dir == Direction.N ? 0xFFFF0000 : 0xFFFFFFFF);
                }
            }
        }
    }

    private static float wrap(float valI) {
        float val = valI % 360.0f;
        if (val >= 180.0f)
            val -= 360.0f;
        if (val < -180.0f)
            val += 360.0f;
        return val;
    }

    private static void glScissor(float x, float y, float x1, float y1) {
        GL11.glScissor((int) (x * Render2DUtil.getScreenScale()), (int) (Render2DUtil.getScreenHeight() - (y1 * Render2DUtil.getScreenScale())), (int) ((x1 - x) * Render2DUtil.getScreenScale()), (int) ((y1 - y) * Render2DUtil.getScreenScale()));
    }

    private static double getPosOnCompass(Direction dir) {
        double yaw = Math.toRadians(MathHelper.wrapDegrees(mc.player.getBodyYaw()));
        int index = dir.ordinal();
        return yaw + (index * Math.PI / 2);
    }

    private double getCompassX(double rad) {
        return Math.sin(rad) * (scale.getValue() * 10);
    }

    private double getCompassY(double rad) {
        final double epicPitch = MathHelper.clamp(mc.player.getPitch() + 30f, -90f, 90f);
        final double pitchRadians = Math.toRadians(epicPitch); // player pitch
        return Math.cos(rad) * Math.sin(pitchRadians) * (scale.getValue() * 10);
    }

    public Compass() {
        super("Compass",  HudCategory.Visual, 60, 70);
        this.setData(new SimpleHudData(this, "Displays a compass"));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        if (compass.getValue() == CompassMode.Line)
            return 100.0f;
        else
            return 23.0f * scale.getValue();
    }

    @Override
    public float getHeight() {
        if (compass.getValue() == CompassMode.Line)
            return 9.0f;
        else
            return 19.0f * scale.getValue();

    }

    private enum CompassMode {
        Line,
        Circle
    }

    private enum Direction {
        N,
        W,
        S,
        E
    }
}
