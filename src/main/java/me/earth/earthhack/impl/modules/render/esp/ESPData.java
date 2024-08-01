package me.earth.earthhack.impl.modules.render.esp;

import me.earth.earthhack.api.module.data.DefaultData;

final class ESPData extends DefaultData<ESP> {

    public ESPData(ESP module) {
        super(module);
        register(module.mode, "The ESP mode");
        register(module.lineWidth, "The width of the ESP lines");
        register(module.range, "The ESP render range");

        register(module.players, "Render players");
        register(module.playersColor, "Players color");
        register(module.friendColor, "Friends color");
        register(module.targetColor, "Targets color");
        register(module.invisibleColor, "Invisible entities color");
        register(module.phaseColor, "Phase entities");
        register(module.pushMode, "The push mode for phase entities");

        register(module.monsters, "Render monsters");
        register(module.monstersColor, "Monsters color");
        register(module.animals, "Render animals");
        register(module.animalsColor, "Animals color");
        register(module.vehicles, "Render vehicles");
        register(module.vehiclesColor, "Vehicles color");
        register(module.misc, "Render misc entities");
        register(module.miscColor, "Misc entities color");

        register(module.storage, "Render block entities");
        register(module.storageRange, "Block entities range");

        register(module.items, "Render items");
        register(module.itemsColor, "Items color");
    }

    @Override
    public String getDescription()
    {
        return "Highlights Players and Entities through walls.";
    }
}
