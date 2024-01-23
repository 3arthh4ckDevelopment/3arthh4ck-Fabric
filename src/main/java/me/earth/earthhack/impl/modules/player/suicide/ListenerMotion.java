package me.earth.earthhack.impl.modules.player.suicide;

import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

final class ListenerMotion extends ModuleListener<Suicide, MotionUpdateEvent>
{
    public ListenerMotion(Suicide module)
    {
        super(module, MotionUpdateEvent.class, 10_000);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.displaying)
        {
            return;
        }

        if (mc.player.getHealth() <= 0.0f)
        {
            module.disable();
            return;
        }

        if (module.mode.getValue() == SuicideMode.Command)
        {
            mc.player.networkHandler.sendChatCommand("kill");
            module.disable();
            return;
        }

        if (!module.autoCrystal.isEnabled())
        {
            module.autoCrystal.enable();
        }

        module.autoCrystal.switching = true;
        if (module.throwAwayTotem.getValue()
            && InventoryUtil.validScreen()
            && module.timer.passed(module.throwDelay.getValue())
            && mc.player.getOffHandStack().getItem()
                == Items.TOTEM_OF_UNDYING)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                mc.interactionManager.clickSlot(
                    0, 45, 1, SlotActionType.THROW, mc.player));
            module.timer.reset();
        }
    }

}
