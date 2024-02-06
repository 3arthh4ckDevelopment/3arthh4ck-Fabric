package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.core.ducks.network.IPlayerInteractEntityC2S;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.sound.SoundEvents;

final class ListenerAttack extends
        ModuleListener<FakePlayer, PacketEvent.Send<PlayerInteractEntityC2SPacket>>
{
    private static final ModuleCache<Criticals> CRITICALS =
            Caches.getModule(Criticals.class);

    public ListenerAttack(FakePlayer module)
    {
        super(module, PacketEvent.Send.class, PlayerInteractEntityC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractEntityC2SPacket> event)
    {
        if (event.isCancelled())
        {
            return;
        }

        Entity entity = ((IPlayerInteractEntityC2S) event.getPacket())
                                                  .getAttackedEntity();
        if (module.fakePlayer.equals(entity))
        {
            event.setCancelled(true);
            if (CRITICALS.isEnabled()
                || !mc.player.isSprinting()
                    && mc.player.fallDistance > 0.0F
                    && !mc.player.onGround
                    && !mc.player.isHoldingOntoLadder()
                    && !mc.player.isSubmergedInWater()
                    && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                    && !mc.player.isRiding())
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
            else if (mc.player.getAttackCooldownProgress(0.5f) > 0.9)
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
            else
            {
                mc.world.playSound(
                        mc.player,
                        mc.player.getX(),
                        mc.player.getY(),
                        mc.player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
                        mc.player.getSoundCategory(),
                        1.0F, 1.0F);
            }
        }
    }

}
