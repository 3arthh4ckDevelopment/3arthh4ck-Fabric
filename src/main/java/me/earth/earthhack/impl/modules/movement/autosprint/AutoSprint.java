package me.earth.earthhack.impl.modules.movement.autosprint;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Direction;

public class AutoSprint extends Module
{
    protected final Setting<SprintMode> mode =
            register(new EnumSetting<>("Mode", SprintMode.Rage));
    protected final Setting<Boolean> faceDirection =
            register(new BooleanSetting("RotationSync", false));

    public AutoSprint()
    {
        super("Sprint", Category.Movement);
        this.listeners.add(new LambdaListener<>(
                UpdateEvent.class, e -> onTick()));
        this.listeners.add(new LambdaListener<>(
                MotionUpdateEvent.class, this::onMotion));
        this.setData(new AutoSprintData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    @Override
    protected void onDisable()
    {
        KeyBinding.setKeyPressed(
                mc.options.sprintKey.getDefaultKey(),
                KeyBoardUtil.isKeyDown(mc.options.sprintKey.getDefaultKey().getCode()));
    }

    public SprintMode getMode()
    {
        return mode.getValue();
    }

    public void onMotion(MotionUpdateEvent event) {
        if (MovementUtil.isMoving()) {
            event.setYaw(mc.player.getMovementDirection().asRotation());
        }
    }

    public void onTick()
    {
        if ((canSprint()
                && (mode.getValue() == SprintMode.Legit))
                || (AutoSprint.canSprintBetter()
                && (mode.getValue() == SprintMode.Rage)))
        {
            mode.getValue().sprint();
        }
    }

    public static boolean canSprint()
    {
        return mc.player != null
                && !mc.player.isSneaking()
                && !mc.player.horizontalCollision
                && MovementUtil.isMoving()
                && ((mc.player.getHungerManager().getFoodLevel() > 6.0f
                    || mc.player.getAbilities().allowFlying)
                        && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS));
    }

    public static boolean canSprintBetter()
    {
        return (mc.options.forwardKey.isPressed()
                || mc.options.backKey.isPressed()
                || mc.options.leftKey.isPressed()
                || mc.options.rightKey.isPressed())
                && !(mc.player == null
                || mc.player.isSneaking()
                || mc.player.horizontalCollision
                || mc.player.getHungerManager().getFoodLevel() <= 6f);
    }

}
