package me.earth.earthhack.impl.hud.text.coordinates;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Random;

public class Coordinates extends HudElement {

    private final Setting<Boolean> dimension =
            register(new BooleanSetting("Opposite-Dimension", true));
    private final Setting<Boolean> random =
            register(new BooleanSetting("SmartRandom", false));
    private final Setting<Integer> randomRange =
            register(new NumberSetting<>("RandomRange", 5000, 1000, 50_000));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));

    private static String coords = "";
    private static Vec3i startingPos = null;
    private static Vec3i realPos = null;
    private final Random rng = new Random();

    private void render(DrawContext context) {
        if (mc.player != null && mc.world != null) {
            final Vec3i pos = smartCoords();
            final long x = pos.getX();
            final long y = pos.getY();
            final long z = pos.getZ();

            String overworld = String.format(Formatting.FORMATTING_CODE_PREFIX + "f%s" + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "f%s" + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "f%s", x, y, z);

            if (dimension.getValue())
                coords = mc.world.getDimension().ultrawarm() ? String.format(Formatting.FORMATTING_CODE_PREFIX + "7%s " + Formatting.FORMATTING_CODE_PREFIX + "f" + actualBracket()[0] + "%s" + actualBracket()[1] + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "7%s" + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "7%s " + Formatting.FORMATTING_CODE_PREFIX + "f" + actualBracket()[0] + "%s" + actualBracket()[1], x, x * 8, y, z, z * 8) : (mc.world.getDimension().bedWorks() ? String.format(Formatting.FORMATTING_CODE_PREFIX + "f%s " + Formatting.FORMATTING_CODE_PREFIX + "7" + actualBracket()[0] + "%s" + actualBracket()[1] + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "f%s" + Formatting.FORMATTING_CODE_PREFIX + "8, " + Formatting.FORMATTING_CODE_PREFIX + "f%s " + Formatting.FORMATTING_CODE_PREFIX + "7" + actualBracket()[0] + "%s" + actualBracket()[1],
                        x,
                        x / 8,
                        y,
                        z,
                        z / 8) : overworld);
            else
                coords = overworld;
        }
        HudRenderUtil.renderText(context, coords, getX(), getY());
    }

    private Vec3i smartCoords() {
        if (MathUtil.distance2D(new Vec3d(mc.player.getX(), 0, mc.player.getZ()), new Vec3d(0,0,0)) < randomRange.getValue()) {
            return new Vec3i((int) Math.round(mc.player.getX()), (int) Math.round(mc.player.getY()), (int) Math.round(mc.player.getZ()));
        }
        if (random.getValue()) {
            if (startingPos == null) {
                realPos = new Vec3i((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
                return startingPos = getRandomVec3i();
            } else if (MathUtil.distance2D(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()), new Vec3d(realPos.getX(), realPos.getY(), realPos.getZ())) < 1000) {
                return new Vec3i((int) (startingPos.getX() - (realPos.getX() - Math.round(mc.player.getX()))), (int) Math.round(mc.player.getY()), (int) (startingPos.getZ() + (realPos.getZ() - Math.round(mc.player.getZ()))));
            } else {
                realPos = new Vec3i((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
                startingPos = getRandomVec3i();
                return startingPos;
            }
        } else {
            return new Vec3i((int) Math.round(mc.player.getX()), (int) Math.round(mc.player.getY()), (int) Math.round(mc.player.getZ()));
        }
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[0] + HudRenderUtil.bracketsTextColor(), HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    private Vec3i getRandomVec3i() {
        return new Vec3i(rng.nextInt(), (int) mc.player.getY(), rng.nextInt());
    }

    public Coordinates() {
        super("Coordinates", HudCategory.Text, 110, 150);
        this.setData(new SimpleHudData(this, "Displays your coordinates."));
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
        return Managers.TEXT.getStringWidth(coords);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
