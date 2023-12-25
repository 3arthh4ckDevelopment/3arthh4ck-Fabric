package me.earth.earthhack.impl.modules.movement.blocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.util.math.BlockPos;

public class ListenerTick extends ModuleListener<BlockLag, TickEvent> {
    public ListenerTick(BlockLag module) {
        super(module, TickEvent.class);
    }
    public static final ModuleCache<BlockLag> burrow =
            Caches.getModule(BlockLag.class);

    @Override
    public void invoke(TickEvent event) {
        if(mc.world == null || mc.player == null){
            return;
        }
        module.target = EntityUtil.getClosestEnemy();
        module.pos = PlayerUtil.getPlayerPos();
        BlockPos posAboveHead = module.pos.add(0,2,0);
        if (    !(Boolean)module.holeOnly.getValue() || PlayerUtil.isInHole(mc.player)
                && !mc.isInSingleplayer()
                && module.target != null
                && !PhaseUtil.isPhasing(module.target, PushMode.MP)
                && !Managers.FRIENDS.contains(module.target)
                && mc.player.distanceTo(module.target) <= module.smartRange.getValue()
                && !PlayerUtil.isInHole(module.target)
                && !burrow.isEnabled()
                && BlockUtil.isReplaceable(module.pos.add(0,1,0))
                // && BlockUtil.isReplaceable(module.pos.add(0,0.2,0))
                && !PhaseUtil.isPhasing(mc.player, PushMode.MP)
                && BlockUtil.isAir(posAboveHead))
        {
            if(!module.delayTimer.passed(module.smartDelay.getValue())){
                return;
            }
            burrow.enable();
            module.delayTimer.reset();
            module.target = null;
            if(module.turnoff.getValue()){
                module.disable();
            }
        }
    }
}
