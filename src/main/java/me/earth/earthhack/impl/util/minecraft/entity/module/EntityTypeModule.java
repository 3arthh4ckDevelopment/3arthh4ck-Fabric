package me.earth.earthhack.impl.util.minecraft.entity.module;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;

// TODO: implement this for ESP, etc.
public class EntityTypeModule extends Module
{
    public final Setting<Boolean> players =
            register(new BooleanSetting("Players", true));
    public final Setting<Boolean> monsters =
            register(new BooleanSetting("Monsters", false));
    public final Setting<Boolean> animals  =
            register(new BooleanSetting("Animals", false));
    public final Setting<Boolean> tamedMobs =
            register(new BooleanSetting("TamedMobs", false));
    public final Setting<Boolean> boss =
            register(new BooleanSetting("Boss", false));
    public final Setting<Boolean> vehicles =
            register(new BooleanSetting("Vehicles", false));
    public final Setting<Boolean> misc     =
            register(new BooleanSetting("Other", false));
    public final Setting<Boolean> entities     =
            register(new BooleanSetting("Entity", false));

    public EntityTypeModule(String name, Category category)
    {
        super(name, category);
        this.setData(new EntityTypeData<>(this));
    }

    public boolean isValid(Entity entity)
    {
        if (entity == null)
        {
            return false;
        }

        if (!tamedMobs.getValue()
                && entity instanceof TameableEntity
                && ((TameableEntity) entity).getOwner() != null)
        {
            return false;
        }

        return switch (((IEntity) entity).earthhack$getType()) {
            case Animal -> animals.getValue();
            case Monster -> monsters.getValue();
            case Player -> players.getValue();
            case Boss -> boss.getValue();
            case Vehicle -> vehicles.getValue();
            case Other -> misc.getValue();
            default -> entities.getValue();
        };
    }

}
