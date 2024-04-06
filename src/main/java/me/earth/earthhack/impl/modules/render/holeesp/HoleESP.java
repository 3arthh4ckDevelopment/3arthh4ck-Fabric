package me.earth.earthhack.impl.modules.render.holeesp;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

//TODO: colors etc. gradient
//TODO: Make HoleManager put 2x1s and 2x2s together so we can draw 1 bb
public class HoleESP extends Module implements HoleObserver
{
    protected final Setting<CalcMode> mode =
            register(new EnumSetting<>("Mode", CalcMode.Polling));

    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 100.0f));
    protected final Setting<Integer> holes =
            register(new NumberSetting<>("Holes", 10, 0, 1000));
    protected final Setting<Integer> safeHole =
            register(new NumberSetting<>("S-Holes", 10, 0, 1000));
    protected final Setting<Integer> wide =
            register(new NumberSetting<>("2x1-Holes", 1, 0, 1000));
    protected final Setting<Integer> big =
            register(new NumberSetting<>("2x2-Holes", 1, 0, 1000));
    protected final Setting<Boolean> fov      =
            register(new BooleanSetting("Fov", true));
    protected final Setting<Boolean> own      =
            register(new BooleanSetting("Own", false));
    protected final Setting<Boolean> fade      =
            register(new BooleanSetting("Fade", false));
    protected final Setting<Float> fadeRange      =
            register(new NumberSetting<>("Fade-Range", 4.0f, 0.0f, 100.0f));
    protected final Setting<Float> minFade      =
            register(new NumberSetting<>("Min-Fade", 3.0f, 0.0f, 100.0f));
    protected final Setting<Double> alphaFactor   =
            register(new NumberSetting<>("AlphaFactor", 0.3, 0.0, 1.0));

    protected final Setting<Float> height     =
            register(new NumberSetting<>("SafeHeight", 1.0f, -1.0f, 1.0f));
    protected final Setting<Float> unsafeHeight =
            register(new NumberSetting<>("UnsafeHeight", 1.0f, -1.0f, 1.0f));
    protected final Setting<Float> wideHeight     =
            register(new NumberSetting<>("2x1-Height", 0.0f, -1.0f, 1.0f));
    protected final Setting<Float> bigHeight     =
            register(new NumberSetting<>("2x2-Height", 0.0f, -1.0f, 1.0f));

    protected final Setting<Color> unsafeColor =
            register(new ColorSetting("UnsafeColor", Color.RED));
    protected final Setting<Color> safeColor =
            register(new ColorSetting("SafeColor", Color.GREEN));
    protected final Setting<Color> wideColor =
            register(new ColorSetting("2x1-Color", new Color(90, 9, 255)));
    protected final Setting<Color> bigColor =
            register(new ColorSetting("2x2-Color", new Color(0, 80, 255)));

    protected final Setting<Boolean> async =
            register(new BooleanSetting("Async", true));
    protected final Setting<Integer> chunk_height =
            register(new NumberSetting<>("Height", 256, 0, 256));
    protected final Setting<Boolean> limit =
            register(new BooleanSetting("Limit", true));
    protected final Setting<Integer> sort_time =
            register(new NumberSetting<>("SortTime", 100, 0, 10_000));
    protected final Setting<Integer> remove_time =
            register(new NumberSetting<>("RemoveTime", 5000, 0, 60_000));

    protected final BlockPos.Mutable mPos = new BlockPos.Mutable();
    // protected final InvalidationHoleManager invalidationHoleManager = new InvalidationHoleManager(this);
    protected final BlockBox bb = new BlockBox(mPos);

    public HoleESP()
    {
        super("HoleESP", Category.Render);
        // this.listeners.add(new ListenerRender(this));
        // this.listeners.addAll(invalidationHoleManager.getListeners());
        this.setData(new HoleESPData(this));
    }

    @Override
    public void onLoad()
    {
        if (this.isEnabled())
        {
            Managers.HOLES.register(this);
        }
    }

    @Override
    public void onEnable()
    {
        Managers.HOLES.register(this);
    }

    @Override
    public void onDisable()
    {
        Managers.HOLES.unregister(this);
        // invalidationHoleManager.get1x1().clear();
        // invalidationHoleManager.getHoles().clear();
        // invalidationHoleManager.get1x1Unsafe().clear();
        // invalidationHoleManager.get2x1().clear();
        // invalidationHoleManager.get2x2().clear();
    }

    protected boolean checkPos(BlockPos pos, BlockPos playerPos)
    {
        return (!fov.getValue() || RotationUtil.inFov(pos))
                && (own.getValue() || !pos.equals(playerPos));
    }

    @Override
    public double getRange()
    {
        return range.getValue();
    }

    @Override
    public int getSafeHoles()
    {
        return safeHole.getValue();
    }

    @Override
    public int getUnsafeHoles()
    {
        return holes.getValue();
    }

    @Override
    public int get2x1Holes()
    {
        return wide.getValue();
    }

    @Override
    public int get2x2Holes()
    {
        return big.getValue();
    }

    public enum CalcMode
    {
        Polling,
        Invalidation
    }

}