package me.earth.earthhack.impl.modules.movement.velocity;

import me.earth.earthhack.api.module.data.DefaultData;

final class VelocityData extends DefaultData<Velocity>
{
    public VelocityData(Velocity velocity)
    {
        super(velocity);
        register(module.knockBack,
            "Block the knockback you take from Hits.");
        register(module.horizontal,
            "The factor of the horizontal knockback you receive.");
        register(module.vertical,
            "The factor of the vertical knockback you receive.");
        register(module.noPush,
            "Prevent getting pushed by other entities.");
        register(module.explosions,
            "Block knockback received from explosions.");
        register(module.bobbers,
            "Block fishing rod bobbers from moving you.");
        register(module.water,
            "Prevent water from pushing you.");
        register(module.blocks,
            "Prevent Blocks from pushing you out e.g. if you phased into one.");
        register(module.shulkers,
            "Prevents Shulkers from pushing you.");
    }

    @Override
    public int getColor()
    {
        return 0xff3048FF;
    }

    @Override
    public String getDescription()
    {
        return "Stops knockback from various sources.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"Anti-KnockBack"};
    }

}
