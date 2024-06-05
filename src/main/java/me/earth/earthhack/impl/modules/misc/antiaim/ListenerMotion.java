package me.earth.earthhack.impl.modules.misc.antiaim;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

final class ListenerMotion extends ModuleListener<AntiAim, MotionUpdateEvent>
{
    private static final Random RANDOM = new Random();
    private int skip;
    int[] numbers;

    public ListenerMotion(AntiAim module)
    {
        super(module, MotionUpdateEvent.class, Integer.MAX_VALUE - 1000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (event.getStage() == Stage.POST || module.dontRotate())
        {
            return;
        }

        if (module.skip.getValue() != 1 && skip++ % module.skip.getValue() == 0)
        {
            event.setYaw(module.lastYaw);
            event.setPitch(module.lastPitch);
            return;
        }

        switch (module.mode.getValue())
        {
            case Random:
                module.lastYaw = (float) ThreadLocalRandom.current()
                        .nextDouble(-180.0, 180.0);
                module.lastPitch = -90.0f + RANDOM.nextFloat() * (180.0f);
                break;
            case Spin:
                module.lastYaw   =
                        (module.lastYaw + module.hSpeed.getValue()) % 360;
                module.lastPitch =
                        (module.lastPitch + module.vSpeed.getValue());
                break;
            case Down:
                module.lastYaw = event.getYaw();
                module.lastPitch = 90.0f;
                break;
            case HeadBang:
                module.lastYaw = event.getYaw();
                module.lastPitch =
                        (module.lastPitch + module.vSpeed.getValue());
                break;
            case Horizontal:
                module.lastPitch = event.getPitch();
                module.lastYaw   =
                        (module.lastYaw + module.hSpeed.getValue()) % 360;
                break;
            case Constant:
                event.setYaw(module.yaw.getValue());
                event.setPitch(module.pitch.getValue());
                return;
            case Flip:
                if (module.flipYaw.getValue())
                {
                    module.lastYaw = (event.getYaw() + 180) % 360;
                }

                if (module.flipPitch.getValue())
                {
                    module.lastPitch = -event.getPitch();
                }
            case ViewLock:

                // yaw
                if (module.sliceYaw.getValue()) {
                    if (module.yawSlices.getValue() == 1) {
                        numbers = new int[] {0};
                    } else if (module.yawSlices.getValue() == 2) {
                        numbers = new int[] {-180, 0};
                    } else if (module.yawSlices.getValue() == 3) {
                        numbers = new int[] {-120, 0, 120};
                    } else {
                        numbers = new int[] {-180, -90, 0, 90};
                    }
                    module.lastYaw = roundToClosest(Math.round(event.getYaw()));
                } else { module.lastYaw = event.getYaw(); }


                // pitch
                if (module.slicePitch.getValue()) {
                    if (module.pitchSlices.getValue() == 1) {
                        numbers = new int[] {0};
                    } else if (module.pitchSlices.getValue() == 2) {
                        numbers = new int[] {-90, 90};
                    } else if (module.pitchSlices.getValue() == 3) {
                        numbers = new int[] {-90, 0, 90};
                    } else {
                        numbers = new int[] {-90, -30, 30, 90};
                    }
                    module.lastPitch = roundToClosest(Math.round(event.getPitch()));
                } else { module.lastYaw = event.getYaw(); }
            default:
        }

        if (module.lastPitch > 90.0f && module.lastPitch != event.getPitch())
        {
            module.lastPitch = -90.0f;
        }

        event.setYaw(module.lastYaw);
        event.setPitch(module.lastPitch);
    }

    private int roundToClosest(int number) {
        int distance = Math.abs(numbers[0] - number);
        int idx = 0;
        for(int c = 1; c < numbers.length; c++){
            int cdistance = Math.abs(numbers[c] - number);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }
}