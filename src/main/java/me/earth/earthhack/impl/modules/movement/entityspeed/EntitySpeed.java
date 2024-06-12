package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class EntitySpeed extends Module
{
    protected final Setting<Float> speed     =
            register(new NumberSetting<>("Speed", 3.8f, 0.1f, 10.0f));
    protected final Setting<Boolean> noStuck =
            register(new BooleanSetting("NoStuck", false));
    protected final Setting<Boolean> resetStuck =
            register(new BooleanSetting("Reset-Stuck", false));
    protected final Setting<Integer> stuckTime     =
            register(new NumberSetting<>("Stuck-Time", 10000, 0, 10000));

    protected final StopWatch stuckTimer = new StopWatch();
    protected final StopWatch jumpTimer  = new StopWatch();
    protected List<BlockPos> positions = new ArrayList<>();

    public EntitySpeed()
    {
        super("EntitySpeed", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.setData(new EntitySpeedData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        return speed.getValue() + "";
    }

    /**
     * Moves {@link net.minecraft.client.MinecraftClient#player}s ridden entity.
     *
     * @param speed the speed to move with.
     */
    public static void strafe(double speed)
    {
        Input input = mc.player.input;
        double forward = input.movementForward;
        double strafe  = input.movementSideways;
        float yaw = mc.player.yaw;
        if (forward == 0.0 && strafe == 0.0)
        {
            //noinspection ConstantConditions
            mc.player.getVehicle().getVelocity().x = 0.0;
            mc.player.getVehicle().getVelocity().z = 0.0;
            return;
        }

        if (forward != 0.0)
        {
            if (strafe > 0.0)
            {
                yaw += forward > 0.0 ? -45 : 45;
            }
            else if (strafe < 0.0)
            {
                yaw += forward > 0.0 ? 45 : -45;
            }

            strafe = 0.0;
            if (forward > 0.0)
            {
                forward = 1.0;
            }
            else if (forward < 0.0)
            {
                forward = -1.0;
            }
        }

        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        //noinspection ConstantConditions
        mc.player.getVehicle().getVelocity().x =
                forward * speed * cos + strafe * speed * sin;
        mc.player.getVehicle().getVelocity().z =
                forward * speed * sin - strafe * speed * cos;
    }

}