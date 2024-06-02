package me.earth.earthhack.impl.modules.render.viewmodel;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.render.MixinHeldItemRenderer;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;

/**
 * @author Cubic
 * @since 14.07.2023
 *
 * TODO: {@link MixinHeldItemRenderer}
 */

public class ViewModel extends Module {

    private final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Items));
    public final Setting<Boolean> noSway =
            register(new BooleanSetting("No-Sway", false));

    private final Setting<Page> pages =
            register(new EnumSetting<>("Pages", Page.Translate));
    /* ---------------- Translate Settings -------------- */
    private final Setting<Double> translateMainX =
            register(new NumberSetting<>("TranslateMainX", 0D, -2D, 2D));
    private final Setting<Double> translateMainY =
            register(new NumberSetting<>("TranslateMainY", 0D, -2D, 2D));
    private final Setting<Double> translateMainZ =
            register(new NumberSetting<>("TranslateMainZ", 0D, -3D, 2D));
    private final Setting<Double> translateOffX =
            register(new NumberSetting<>("TranslateOffX", 0D, -2D, 2D));
    private final Setting<Double> translateOffY =
            register(new NumberSetting<>("TranslateOffY", 0D, -3D, 2D));
    private final Setting<Double> translateOffZ =
            register(new NumberSetting<>("TranslateOffZ", 0D, -2D, 2D));
    /* ---------------- Scale Settings -------------- */
    private final Setting<Float> scaleMainX =
            register(new NumberSetting<>("ScaleMainX", 1f, -2f, 2f));
    private final Setting<Float> scaleMainY =
            register(new NumberSetting<>("ScaleMainY", 1f, -2f, 2f));
    private final Setting<Float> scaleMainZ =
            register(new NumberSetting<>("ScaleMainZ", 1f, -2f, 2f));
    private final Setting<Float> scaleOffX =
            register(new NumberSetting<>("ScaleOffX", 1f, -2f, 2f));
    private final Setting<Float> scaleOffY =
            register(new NumberSetting<>("ScaleOffY", 1f, -2f, 2f));
    private final Setting<Float> scaleOffZ =
            register(new NumberSetting<>("ScaleOffZ", 1f, -2f, 2f));
    /* ---------------- Rotate Settings -------------- */
    private final Setting<Double> rotateMainX =
            register(new NumberSetting<>("RotateMainX", 0D, 0D, 360D));
    private final Setting<Double> rotateMainY =
            register(new NumberSetting<>("RotateMainY", 0D, 0D, 360D));
    private final Setting<Double> rotateMainZ =
            register(new NumberSetting<>("RotateMainZ", 0D, 0D, 360D));
    private final Setting<Double> rotateOffX =
            register(new NumberSetting<>("RotateOffX", 0D, 0D, 360D));
    private final Setting<Double> rotateOffY =
            register(new NumberSetting<>("RotateOffY", 0D, 0D, 360D));
    private final Setting<Double> rotateOffZ =
            register(new NumberSetting<>("RotateOffZ", 0D, 0D, 360D));
    /* ---------------- Swing Settings -------------- */
    private final Setting<Double> swingMain =
            register(new NumberSetting<>("SwingMain", 0D, -1D, 1D));
    private final Setting<Double> swingOff =
            register(new NumberSetting<>("SwingOff", 0D, -1D, 1D));


    public ViewModel() {
        super("ViewModel", Category.Render);

        new PageBuilder<>(this, pages)
                .addPage(page -> page == Page.Translate, translateMainX, translateOffZ)
                .addPage(page -> page == Page.Scale, scaleMainX, scaleOffZ)
                .addPage(page -> page == Page.Rotate, rotateMainX, rotateOffZ)
                .addPage(page -> page == Page.Swing, swingMain, swingOff)
                .register(Visibilities.VISIBILITY_MANAGER);
        SimpleData data = new SimpleData(this, "Changes how the held items look");
        data.register(noSway, "Changes how the held items look");
        this.setData(data);
    }

    public void doTransform(MatrixStack matrix, Arm side) {
        if (side == Arm.LEFT) {
            matrix.scale(scaleOffX.getValue(), scaleOffY.getValue(), scaleOffZ.getValue());
            matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotateOffX.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotateOffX.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotateOffX.getValue().floatValue()));
            matrix.translate(translateOffX.getValue(), translateOffY.getValue(), translateOffZ.getValue());
        } else {
            matrix.scale(scaleMainX.getValue(), scaleMainY.getValue(), scaleMainZ.getValue());
            matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotateMainX.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotateMainY.getValue().floatValue()));
            matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotateMainZ.getValue().floatValue()));
            matrix.translate(translateMainX.getValue(), translateMainY.getValue(), translateMainZ.getValue());
        }
    }

    public float getSwing(Hand hand) {
        return hand == Hand.MAIN_HAND ? swingMain.getValue().floatValue() : swingOff.getValue().floatValue();
    }

    public boolean isItems() {
        return mode.getValue() == Mode.Items;
    }

    public boolean isHand() {
        return mode.getValue() == Mode.Hands;
    }

    private enum Page {
        Translate,
        Scale,
        Rotate,
        Swing
    }

    private enum Mode {
        Items,
        Hands,
    }

}
