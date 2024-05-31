package me.earth.earthhack.impl.modules.combat.bowspam;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerMotion extends ModuleListener<BowSpam, MotionUpdateEvent> {
    private float lastTimer = -1.f;

    public ListenerMotion(BowSpam module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            ItemStack stack = getStack();
            if (module.spam.getValue()) {
                if (mc.player.isOnGround()) {
                    if (stack != null
                            && !mc.player.getActiveItem().isEmpty()
                            && mc.player.getItemUseTimeLeft() > 0) {

                        Managers.TIMER.setTimer(6.0f);

                        if (stack.getMaxDamage() - mc.player.getItemUseTimeLeft() > (module.delay.getValue() * 6)) {
                            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                                    mc.interactionManager.stopUsingItem(mc.player));
                        }
                    } else {
                        if (lastTimer > 0 && Managers.TIMER.getSpeed() != lastTimer) {
                            Managers.TIMER.setTimer(lastTimer);
                        }
                        lastTimer = Managers.TIMER.getSpeed();
                    }
                }
            } else {
                if (lastTimer > 0 && Managers.TIMER.getSpeed() != lastTimer) {
                    Managers.TIMER.setTimer(lastTimer);
                    lastTimer = 1.f;
                }
                if (stack != null // check if stack.equals(mc.player.getActive...)?
                        && !mc.player.getActiveItem().isEmpty()
                        && (stack.getMaxDamage()
                        - mc.player.getItemUseTimeLeft())
                        - (module.tpsSync.getValue()
                        ? 20.0f - Managers.TPS.getTps()
                        : 0.0f) >= module.delay.getValue()) {
                    if (module.bowBomb.getValue()) {
                        NetworkUtil.sendPacketNoEvent(new PlayerMoveC2SPacket
                                .Full(mc.player.getX(),
                                mc.player.getY() - 0.0624,
                                mc.player.getZ(),
                                mc.player.getHeadYaw(),
                                mc.player.getPitch(),
                                false));

                        NetworkUtil.sendPacketNoEvent(new PlayerMoveC2SPacket
                                .Full(mc.player.getX(),
                                mc.player.getY() - 999.0,
                                mc.player.getZ(),
                                mc.player.getHeadYaw(),
                                mc.player.getPitch(),
                                true));
                    }

                    Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                            mc.interactionManager.stopUsingItem(mc.player));
                }
            }
        }
    }

    private ItemStack getStack() {
        ItemStack mainHand = mc.player.getMainHandStack();

        if (mainHand.getItem() instanceof BowItem) {
            return mainHand;
        }

        ItemStack offHand = mc.player.getOffHandStack();

        if (offHand.getItem() instanceof BowItem) {
            return offHand;
        }

        return null;
    }

}
