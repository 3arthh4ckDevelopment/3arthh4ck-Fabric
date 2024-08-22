package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.combat.antisurround.AntiSurround;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotateMode;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.RotationFunction;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MouseFilter;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion extends
        ModuleListener<AutoCrystal, MotionUpdateEvent>
{
    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final MouseFilter yawMouseFilter = new MouseFilter();

    public ListenerMotion(AutoCrystal module) {
        super(module, MotionUpdateEvent.class, 1500);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (AbstractCalculation.ANTISURROUND
                .returnIfPresent(AntiSurround::isActive, false))
        {
            return;
        }

        if (event.getStage() == Stage.PRE)
        {
            if (!module.multiThread.getValue()
                    && module.motionCalc.getValue()
                    && (Managers.POSITION.getX() != event.getX()
                    || Managers.POSITION.getY() != event.getY()
                    || Managers.POSITION.getZ() != event.getZ())) {
                CalculationMotion calc = new CalculationMotion(module,
                        Managers.ENTITIES.getEntities(),
                        Managers.ENTITIES.getPlayers());
                module.threadHelper.start(calc, false);
            } else {
                if (module.motionThread.getValue()
                    && module.pullTimer.passed(module.pullBasedDelay.getValue())) {
                    module.threadHelper.startThread();
                    module.pullTimer.reset();
                }
            }

            AbstractCalculation<?> current =
                    module.threadHelper.getCurrentCalc();
            if (current != null
                    && !current.isFinished()
                    && module.rotate.getValue() != ACRotate.None
                    && module.rotationThread.getValue() == RotationThread.Wait) {
                synchronized (module) {
                    try {
                        module.wait(module.timeOut.getValue());
                    } catch (InterruptedException e)
                    {
                        Earthhack.getLogger()
                                .warn("Minecraft Main-Thread interrupted!");
                        Thread.currentThread().interrupt();
                    }
                }
            }

            RotationFunction rotation = module.rotation;
            if (rotation != null) {
                module.isSpoofing = true;
                float[] rotations = rotation.apply(event.getX(),
                        event.getY(),
                        event.getZ(),
                        event.getYaw(),
                        event.getPitch());

                if (module.rotateMode.getValue() == RotateMode.Smooth) {
                    final float yaw = (yawMouseFilter.smooth(rotations[0] + MathUtil.getRandomInRange(-1.0f, 5.0f), module.smoothSpeed.getValue()));
                    final float pitch = (pitchMouseFilter.smooth(rotations[1] + MathUtil.getRandomInRange(-1.20f, 3.50f), module.smoothSpeed.getValue()));
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                } else {
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
            } else if (module.rayTraceBypass.getValue()
                && module.forceBypass.getValue()) {
                BlockPos bypassPos = module.getBypassPos();
                if (bypassPos != null) {
                    float[] rotations =
                        RotationUtil.getRotationsToTopMiddleUp(bypassPos);
                    float pitch =
                        rotations[1] == 0.0f && module.rbYaw.getValue() != 0.0f
                            ? 0.0f
                            : rotations[1] < 0.0f
                                ? rotations[1] + module.rbPitch.getValue()
                                : rotations[1] - module.rbPitch.getValue();

                    event.setYaw((rotations[0] + module.rbYaw.getValue()) % 360);
                    event.setPitch(pitch);
                }
            }
        } else {
            module.motionID.incrementAndGet();
            synchronized (module.post) {
                module.runPost();
            }

            module.isSpoofing = false;
        }
    }

    @Override
    public int getPriority() {
        return module.priority.getValue();
    }

}
