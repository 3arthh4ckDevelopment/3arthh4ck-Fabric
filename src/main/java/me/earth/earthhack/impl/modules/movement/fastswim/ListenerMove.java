package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

final class ListenerMove extends ModuleListener<FastSwim, MoveEvent>
{
    public ListenerMove(FastSwim module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.strafe.getValue())
        {
            if (!module.accelerate.getValue() && Managers.NCP.passed(250))
            {
                if (!mc.player.onGround)
                {
                    if (mc.player.isInLava())
                    {
                        MovementUtil.strafe(event, module.hLava.getValue());
                        if (!module.fall.getValue())
                        {
                            if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.options.jumpKey.isPressed())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                    else if (mc.player.isTouchingWater())
                    {
                        MovementUtil.strafe(event, module.hLava.getValue());
                        if (!module.fall.getValue())
                        {
                            if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.options.jumpKey.isPressed())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                }
            }
            else if (module.accelerate.getValue())
            {
                if (!mc.player.onGround)
                {
                    if (Managers.NCP.passed(250))
                    {
                        if (mc.player.isInLava())
                        {
                            module.waterSpeed *= module.accelerateFactor.getValue();
                        }
                        else if (mc.player.isTouchingWater())
                        {
                            module.lavaSpeed *= module.accelerateFactor.getValue();
                        }
                    }
                    if (mc.player.isInLava())
                    {
                        MovementUtil.strafe(event, module.lavaSpeed);
                        if (!module.fall.getValue())
                        {
                            if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                    else if (mc.player.isTouchingWater())
                    {
                        MovementUtil.strafe(event, module.waterSpeed);
                        if (!module.fall.getValue())
                        {
                            if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.options.sneakKey.isPressed())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                }
                else
                {
                    module.waterSpeed = module.hWater.getValue();
                    module.lavaSpeed = module.hLava.getValue();
                }
            }
        }
        else
        {
            if (Managers.NCP.passed(250) && !mc.player.onGround)
            {
                if (mc.player.isInLava())
                {
                    event.setX(event.getX() * module.hLava.getValue());
                    event.setY(event.getY() * module.vLava.getValue());
                    event.setZ(event.getZ() * module.hLava.getValue());
                }
                else if (mc.player.isTouchingWater())
                {
                    event.setX(event.getX() * module.hWater.getValue());
                    event.setY(event.getY() * module.vWater.getValue());
                    event.setZ(event.getZ() * module.hWater.getValue());
                }
            }
        }
    }

}
