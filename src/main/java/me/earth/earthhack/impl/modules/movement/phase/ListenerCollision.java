package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.CollisionEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Blocks;

final class ListenerCollision extends ModuleListener<Phase, CollisionEvent>
{
    public ListenerCollision(Phase module)
    {
        super(module, CollisionEvent.class);
    }

    @Override
    public void invoke(CollisionEvent event)
    {
        if (mc.player == null
                || mc.player.input == null
                || !mc.player.equals(event.getEntity()))
        {
            return;
        }

        switch (module.mode.getValue())
        {
            case Constantiam:
                if (event.getBB() != null
                        && event.getBB().maxY > mc.player.getBoundingBox().minY
                        && mc.world.getBlockState(PositionUtil.getPosition().up()).getBlock() != Blocks.AIR) // shit collision check, otherwise causes stack overflow
                {
                    // PacketUtil.sendAction(CPacketEntityAction.Action.START_SNEAKING);
                    event.setBB(null);
                    // PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SNEAKING);
                }
                break;
            case ConstantiamNew:
                if (module.isPhasing()) {
                    event.setBB(null);
                }
                break;
            case Normal:
                if (module.onlyBlock.getValue()
                        && !module.isPhasing()
                        || module.autoClick.getValue()
                        && module.requireClick.getValue()
                        && module.clickBB.getValue()
                        && !module.clickTimer.passed(
                        module.clickDelay.getValue())
                        || module.forwardBB.getValue()
                        && !mc.options.forwardKey.isPressed())
                {
                    return;
                }

                if (event.getBB() != null
                        && event.getBB().maxY >
                        mc.player.getBoundingBox().minY
                        && mc.player.isSneaking())
                {
                    event.setBB(null);
                }

                break;
            case Sand:
                event.setBB(null);
                mc.player.noClip = true;
                break;
            case Climb:
                if (mc.player.horizontalCollision)
                {
                    event.setBB(null);
                }

                if (mc.player.input.sneaking
                        || (mc.player.input.jumping
                        && event.getPos().getY() > mc.player.getY()))
                {
                    event.setCancelled(true);
                }

                break;
            default:
        }
    }

}