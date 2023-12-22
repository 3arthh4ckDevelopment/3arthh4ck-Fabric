package me.earth.earthhack.impl.modules.movement.longjump;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.movement.longjump.mode.JumpMode;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerTick extends ModuleListener<LongJump, TickEvent>
{
    private static final double[] MOVE =
    {
        0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361,
        0.404761, 0.402178, 0.399611, 0.397060, 0.394525, 0.392000,
        0.389400, 0.386440, 0.383655, 0.381105, 0.37867, 0.37625,
        0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.359450,
        0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333,
        0.33, 0.327000, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309,
        0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289,
        0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271,
        0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253,
        0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237
    };

    public ListenerTick(LongJump module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.player == null
                || mc.world == null
                || module.mode.getValue() != JumpMode.Cowabunga)
        {
            return;
        }

        if (KeyBoardUtil.isKeyDown(module.invalidBind))
        {
            module.invalidPacket();
        }

        if (MovementUtil.noMovementKeys())
        {
            return;
        }

        float direction = mc.player.getYaw() +
                         (mc.player.forwardSpeed < 0 ? 180 : 0)
                            + (mc.player.sidewaysSpeed > 0
                                ? (-90 * (mc.player.forwardSpeed < 0
                                    ? -0.5F
                                    : (mc.player.forwardSpeed > 0
                                        ? 0.5F
                                        : 1)))
                                : 0)
                            - (mc.player.sidewaysSpeed < 0
                                ? (-90 * (mc.player.forwardSpeed < 0
                                    ? -0.5F
                                    : (mc.player.forwardSpeed > 0
                                        ? 0.5F
                                        : 1)))
                                : 0),

        x = (float) Math.cos((direction + 90) * Math.PI / 180),
        z = (float) Math.sin((direction + 90) * Math.PI / 180);

        if (!mc.player.verticalCollision)
        {
            module.airTicks++;
            if (mc.player.input.sneaking)
            {
                module.invalidPacket();
            }

            module.groundTicks = 0;
            if (!mc.player.verticalCollision)
            {
                if (mc.player.getVelocity().y == -0.07190068807140403)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.3499999940395355, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.10306193759436909)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.550000011920929, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.13395038817442878)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.6700000166893005, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.16635183030382)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.6899999976158142, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.19088711097794803)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.7099999785423279, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.21121925191528862)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.20000000298023224, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.11979897632390576)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.9300000071525574, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.18758479151225355)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.7200000286102295, mc.player.getVelocity().z);
                }

                if (mc.player.getVelocity().y == -0.21075983825251726)
                {
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.7599999904632568, mc.player.getVelocity().z);
                }

                if (module.getDistance(mc.player, 69.0) < 0.5)
                {
                    if (mc.player.getVelocity().y == -0.23537393014173347)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.029999999329447746, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08531999505205401)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * -0.5, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.03659320313669756)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * -0.10000000149011612, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.07481386749524899)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * -0.07000000029802322, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.0732677700939672)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * -0.05000000074505806, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.07480988066790395)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.03999999910593033, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.0784000015258789)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.10000000149011612, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08608320193943977)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.10000000149011612, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08683615560584318)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.05000000074505806, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08265497329678266)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.05000000074505806, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08245009535659828)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.05000000074505806, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08244005633718426)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, -0.08243956442521608, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y == -0.08243956442521608)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, -0.08244005590677261, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y > -0.1
                            && mc.player.getVelocity().y < -0.08
                            && !mc.player.isOnGround()
                            && mc.player.input.pressingForward)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, -9.999999747378752E-5, mc.player.getVelocity().z);
                    }
                }
                else
                {
                    if (mc.player.getVelocity().y < -0.2 && mc.player.getVelocity().y > -0.24)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.7, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y < -0.25 && mc.player.getVelocity().y > -0.32)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.8, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y < -0.35 && mc.player.getVelocity().y > -0.8)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.98, mc.player.getVelocity().z);
                    }

                    if (mc.player.getVelocity().y < -0.8 && mc.player.getVelocity().y > -1.6)
                    {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * 0.99, mc.player.getVelocity().z);
                    }
                }
            }

            Managers.TIMER.setTimer(0.85f);
            if (mc.player.input.pressingForward)
            {
                try
                {
                    mc.player.setVelocity(x * MOVE[module.airTicks - 1] * 3.0, mc.player.getVelocity().y, z * MOVE[module.airTicks - 1] * 3.0);
                    return;
                }
                catch (Exception ex)
                {
                    return;
                }
            }

            mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);
            return;
        }

        Managers.TIMER.setTimer(1.0f);
        module.airTicks = 0;
        module.groundTicks++;
        mc.player.setVelocity(mc.player.getVelocity(). x / 13.0, mc.player.getVelocity().y, mc.player.getVelocity().z / 13.0);

        if (module.groundTicks == 1)
        {
            module.updatePosition(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            module.updatePosition(
                    mc.player.getX() + 0.0624, mc.player.getY(), mc.player.getZ());
            module.updatePosition(
                    mc.player.getX(), mc.player.getY() + 0.419, mc.player.getZ());
            module.updatePosition(
                    mc.player.getX() + 0.0624, mc.player.getY(), mc.player.getZ());
            module.updatePosition(
                    mc.player.getX(), mc.player.getY() + 0.419, mc.player.getZ());
        }

        if (module.groundTicks > 2)
        {
            module.groundTicks = 0;
            mc.player.setVelocity(x * 0.3, 0.42399999499320984, z * 0.3);
        }
    }

}
