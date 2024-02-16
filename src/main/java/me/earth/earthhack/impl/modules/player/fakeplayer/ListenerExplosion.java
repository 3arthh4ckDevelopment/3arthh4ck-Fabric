package me.earth.earthhack.impl.modules.player.fakeplayer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

final class ListenerExplosion extends
        ModuleListener<FakePlayer, PacketEvent.Receive<ExplosionS2CPacket>>
{
    public ListenerExplosion(FakePlayer module)
    {
        super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<ExplosionS2CPacket> event)
    {
        if (module.damage.getValue())
        {
            mc.execute(() -> handleExplosion(event.getPacket()));
        }
    }

    private void handleExplosion(ExplosionS2CPacket packet)
    {
        if (mc.world == null
            || module.fakePlayer == null
            || !module.isEnabled())
        {
            return;
        }

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();

        double distance = Math.sqrt(module.fakePlayer.squaredDistanceTo(x, y, z) / 12.0);
        if (distance > 1.0)
        {
            return;
        }

        float size = packet.getRadius();
        double density = DamageUtil.getBlockDensity(new Vec3d(x, y, z),
                module.fakePlayer.getBoundingBox(), mc.world,
                true, false, false, false);

        double densityDistance = distance = (1.0 - distance) * density;
        float damage = (float) ((densityDistance * densityDistance + distance)
                                    / 2.0 * 7.0 * size * 2.0f + 1.0);
        DamageSource damageSource = (
                new Explosion(mc.world, mc.player, x, y, z, size, false, Explosion.DestructionType.DESTROY)
                        .getCausingEntity().getRecentDamageSource());

        float limbSwing = module.fakePlayer.handSwingProgress;
        module.fakePlayer.damage(damageSource, damage);
        module.fakePlayer.handSwingProgress = limbSwing;
    }

}
