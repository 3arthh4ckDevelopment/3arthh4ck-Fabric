package me.earth.earthhack.impl.modules.render.logoutspots.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.TimeStamp;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class LogoutSpot extends TimeStamp implements Globals
{
    private final String name;
    private final StaticModelPlayer<PlayerEntity> model;
    private final Box boundingBox;
    private final double x;
    private final double y;
    private final double z;

    public LogoutSpot(PlayerEntity player)
    {
        this.name = player.getName().toString();
        this.model = new StaticModelPlayer<>(PlayerUtil.copyPlayer(player),
                player instanceof AbstractClientPlayerEntity && (player).getType().equals("slim"),
                null);
        this.model.disableArmorLayers();
        this.boundingBox = player.getBoundingBox();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    public String getName()
    {
        return name;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public double getDistance()
    {
        return mc.player.distanceTo(model.getPlayer());
    }

    public Box getBoundingBox()
    {
        return boundingBox;
    }

    public StaticModelPlayer<PlayerEntity> getModel() {
        return model;
    }

    public Vec3d rounded()
    {
        return new Vec3d(MathUtil.round(x, 1), MathUtil.round(y, 1), MathUtil.round(z, 1));
    }

}
